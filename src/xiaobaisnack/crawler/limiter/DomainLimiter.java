package xiaobaisnack.crawler.limiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ⭐ 按域名限流器
 *
 * 功能：
 * - 为每个域名独立限流
 * - 避免对同一域名请求过快
 * - 线程安全
 */
public class DomainLimiter {

    private final Map<String, TokenBucketLimiter> limiters = new ConcurrentHashMap<>();
    private final double defaultRate;
    private final long defaultCapacity;

    /**
     * 构造域名限流器
     * @param defaultRate 默认每秒请求数
     */
    public DomainLimiter(double defaultRate) {
        this(defaultRate, (long) (defaultRate * 2));
    }

    /**
     * 构造域名限流器
     * @param defaultRate 默认每秒请求数
     * @param defaultCapacity 默认桶容量
     */
    public DomainLimiter(double defaultRate, long defaultCapacity) {
        this.defaultRate = defaultRate;
        this.defaultCapacity = defaultCapacity;
    }

    /**
     * 提取域名
     */
    private String extractDomain(String url) {
        try {
            String temp = url.replace("http://", "").replace("https://", "");
            int slashIndex = temp.indexOf('/');
            if (slashIndex > 0) {
                temp = temp.substring(0, slashIndex);
            }
            return temp;
        } catch (Exception e) {
            return url;
        }
    }

    /**
     * 获取或创建域名对应的限流器
     */
    private TokenBucketLimiter getLimiter(String domain) {
        return limiters.computeIfAbsent(domain,
                k -> new TokenBucketLimiter(defaultRate, defaultCapacity));
    }

    /**
     * 尝试获取指定URL对应域名的许可
     * @param url 目标URL
     * @return true表示允许请求
     */
    public boolean tryAcquire(String url) {
        String domain = extractDomain(url);
        return getLimiter(domain).tryAcquire();
    }

    /**
     * 阻塞等待获取许可
     * @param url 目标URL
     */
    public void acquire(String url) {
        String domain = extractDomain(url);
        getLimiter(domain).acquire();
    }

    /**
     * 为指定域名设置自定义速率
     * @param domain 域名
     * @param rate 每秒请求数
     */
    public void setDomainRate(String domain, double rate) {
        TokenBucketLimiter limiter = limiters.get(domain);
        if (limiter != null) {
            limiter.setRate(rate);
        } else {
            limiters.put(domain, new TokenBucketLimiter(rate, (long) (rate * 2)));
        }
    }

    /**
     * 获取指定域名的限流器
     */
    public TokenBucketLimiter getDomainLimiter(String domain) {
        return limiters.get(domain);
    }

    /**
     * 获取管理的域名数量
     */
    public int getDomainCount() {
        return limiters.size();
    }
}