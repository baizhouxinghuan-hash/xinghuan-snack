package xiaobaisnack.crawler.limiter;

/**
 * ⭐ 通用速率限制器接口
 *
 * 功能：
 * - 定义限流器的基本行为
 * - 控制请求频率，避免被封IP
 */
public interface RateLimiter {

    /**
     * 尝试获取许可
     * @return true表示允许请求，false表示需要等待
     */
    boolean tryAcquire();

    /**
     * 阻塞等待直到获取许可
     */
    void acquire();

    /**
     * 获取当前速率（每秒请求数）
     */
    double getRate();

    /**
     * 设置速率
     * @param permitsPerSecond 每秒允许的请求数
     */
    void setRate(double permitsPerSecond);
}