package xiaobaisnack.crawler.storage.entity;

/**
 * 页面实体。
 */
public class PageEntity {

    /** 结果来源：本地数据库 / 外部搜索 */
    public static final String SOURCE_LOCAL = "LOCAL";
    public static final String SOURCE_EXTERNAL = "EXTERNAL";

    private int id;
    private String url;
    private String title;
    private String content;
    private String status;
    private String crawlTime;
    private String source = SOURCE_LOCAL;

    public PageEntity(String url, String title, String content) {
        this(0, url, title, content, "DONE", "");
    }

    public PageEntity(int id, String url, String title, String content) {
        this(id, url, title, content, "DONE", "");
    }

    public PageEntity(String url, String title, String content, String status, String crawlTime) {
        this(0, url, title, content, status, crawlTime);
    }

    public PageEntity(int id, String url, String title, String content, String status, String crawlTime) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.content = content;
        this.status = status == null ? "DONE" : status;
        this.crawlTime = crawlTime == null ? "" : crawlTime;
    }

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
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCrawlTime() {
        return crawlTime;
    }

    public void setCrawlTime(String crawlTime) {
        this.crawlTime = crawlTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}