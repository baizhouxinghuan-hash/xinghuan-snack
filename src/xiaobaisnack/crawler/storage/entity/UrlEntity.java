package xiaobaisnack.crawler.storage.entity;

/**
 * ⭐ URL信息实体
 *
 * 功能：
 * - 存储URL的元信息
 * - 包含状态、深度、优先级等
 */
public class UrlEntity {

    private int id;
    private String url;
    private String normalizedUrl;
    private String domain;
    private int depth;
    private int priority;
    private String state;       // NEW, QUEUED, FETCHING, DONE, FAILED
    private long createTime;
    private long updateTime;
    private int retryCount;
    private String errorMessage;

    public UrlEntity() {
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
        this.state = "NEW";
        this.depth = 0;
        this.priority = 5;
        this.retryCount = 0;
    }

    public UrlEntity(String url) {
        this();
        this.url = url;
        this.normalizedUrl = normalizeUrl(url);
        this.domain = extractDomain(url);
    }

    public UrlEntity(int id, String url, String state, int depth, int priority) {
        this(url);
        this.id = id;
        this.state = state;
        this.depth = depth;
        this.priority = priority;
    }

    /**
     * URL规范化
     */
    private String normalizeUrl(String url) {
        if (url == null) return null;
        String normalized = url.trim();
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized.toLowerCase();
    }

    /**
     * 提取域名
     */
    private String extractDomain(String url) {
        if (url == null) return "";
        try {
            String temp = url.replace("http://", "").replace("https://", "");
            int slashIndex = temp.indexOf('/');
            if (slashIndex > 0) {
                return temp.substring(0, slashIndex);
            }
            return temp;
        } catch (Exception e) {
            return "";
        }
    }

    // ===== Getters & Setters =====

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        this.normalizedUrl = normalizeUrl(url);
        this.domain = extractDomain(url);
    }

    public String getNormalizedUrl() {
        return normalizedUrl;
    }

    public String getDomain() {
        return domain;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        this.updateTime = System.currentTimeMillis();
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public void incrementRetry() {
        this.retryCount++;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "UrlEntity{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", domain='" + domain + '\'' +
                ", depth=" + depth +
                ", priority=" + priority +
                ", state='" + state + '\'' +
                ", retryCount=" + retryCount +
                '}';
    }
}