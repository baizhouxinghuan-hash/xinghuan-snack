package xiaobaisnack.crawler.limiter;

/**
 * ⭐ 令牌桶限流器
 *
 * 功能：
 * - 使用令牌桶算法实现限流
 * - 平滑突发流量
 * - 线程安全
 *
 * 算法原理：
 * - 以固定速率向桶中添加令牌
 * - 每个请求需要消耗一个令牌
 * - 桶满时丢弃多余令牌
 * - 无令牌时拒绝请求或等待
 */
public class TokenBucketLimiter implements RateLimiter {

    private final long capacity;          // 桶容量（最大令牌数）
    private double tokens;                // 当前令牌数
    private double rate;                  // 令牌生成速率（每秒）
    private long lastRefillTime;          // 上次填充时间
    private final Object lock = new Object();

    /**
     * 构造令牌桶限流器
     * @param permitsPerSecond 每秒生成的令牌数
     */
    public TokenBucketLimiter(double permitsPerSecond) {
        this(permitsPerSecond, (long) (permitsPerSecond * 2));
    }

    /**
     * 构造令牌桶限流器
     * @param permitsPerSecond 每秒生成的令牌数
     * @param capacity 桶容量
     */
    public TokenBucketLimiter(double permitsPerSecond, long capacity) {
        this.rate = permitsPerSecond;
        this.capacity = capacity;
        this.tokens = capacity;
        this.lastRefillTime = System.nanoTime();
    }

    /**
     * 填充令牌
     */
    private void refill() {
        long now = System.nanoTime();
        double elapsed = (now - lastRefillTime) / 1_000_000_000.0; // 转换为秒
        double newTokens = elapsed * rate;

        tokens = Math.min(capacity, tokens + newTokens);
        lastRefillTime = now;
    }

    @Override
    public boolean tryAcquire() {
        synchronized (lock) {
            refill();
            if (tokens >= 1.0) {
                tokens -= 1.0;
                return true;
            }
            return false;
        }
    }

    @Override
    public void acquire() {
        while (!tryAcquire()) {
            try {
                // 等待约一个令牌生成的时间
                long waitMs = (long) (1000.0 / rate);
                Thread.sleep(Math.max(waitMs, 10));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public double getRate() {
        return rate;
    }

    @Override
    public void setRate(double permitsPerSecond) {
        synchronized (lock) {
            this.rate = permitsPerSecond;
        }
    }

    /**
     * 获取当前可用令牌数
     */
    public double getAvailableTokens() {
        synchronized (lock) {
            refill();
            return tokens;
        }
    }

    /**
     * 获取桶容量
     */
    public long getCapacity() {
        return capacity;
    }
}