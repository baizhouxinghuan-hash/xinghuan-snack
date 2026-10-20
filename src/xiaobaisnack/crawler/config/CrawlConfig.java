package xiaobaisnack.crawler.config;

/**
 * ⭐ 爬虫配置类
 *
 * 功能：
 * - 管理爬虫运行参数
 * - 线程数、深度、延迟等配置
 */
public class CrawlConfig {

    private int maxDepth = 3;           // 最大爬取深度
    private int maxThreads = 4;         // 最大线程数
    private int maxUrls = 100;          // 最大抓取URL数
    private int requestDelay = 1000;    // 请求间隔(ms)
    private int connectTimeout = 5000;  // 连接超时(ms)
    private int readTimeout = 5000;     // 读取超时(ms)
    private String userAgent = "Mozilla/5.0 (compatible; BaiZhouXingHuan/1.0)";
    private boolean respectRobotsTxt = true;  // 是否遵守robots.txt
    private boolean enableRateLimit = true;   // 是否启用限流
    private int rateLimitPerSecond = 5;       // 每秒最大请求数

    // ===== Getters & Setters =====

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public int getMaxUrls() {
        return maxUrls;
    }

    public void setMaxUrls(int maxUrls) {
        this.maxUrls = maxUrls;
    }

    public int getRequestDelay() {
        return requestDelay;
    }

    public void setRequestDelay(int requestDelay) {
        this.requestDelay = requestDelay;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public boolean isRespectRobotsTxt() {
        return respectRobotsTxt;
    }

    public void setRespectRobotsTxt(boolean respectRobotsTxt) {
        this.respectRobotsTxt = respectRobotsTxt;
    }

    public boolean isEnableRateLimit() {
        return enableRateLimit;
    }

    public void setEnableRateLimit(boolean enableRateLimit) {
        this.enableRateLimit = enableRateLimit;
    }

    public int getRateLimitPerSecond() {
        return rateLimitPerSecond;
    }

    public void setRateLimitPerSecond(int rateLimitPerSecond) {
        this.rateLimitPerSecond = rateLimitPerSecond;
    }

    @Override
    public String toString() {
        return "CrawlConfig{" +
                "maxDepth=" + maxDepth +
                ", maxThreads=" + maxThreads +
                ", maxUrls=" + maxUrls +
                ", requestDelay=" + requestDelay +
                ", connectTimeout=" + connectTimeout +
                ", readTimeout=" + readTimeout +
                ", userAgent='" + userAgent + '\'' +
                ", respectRobotsTxt=" + respectRobotsTxt +
                ", enableRateLimit=" + enableRateLimit +
                ", rateLimitPerSecond=" + rateLimitPerSecond +
                '}';
    }
}