package xiaobaisnack.crawler.ui.model;

import xiaobaisnack.crawler.storage.entity.PageEntity;

/**
 * ⭐ URL视图模型
 *
 * 功能：
 * - 封装URL展示数据
 * - 用于UI表格显示
 */
public class UrlViewModel {

    private final int id;
    private final String url;
    private final String title;
    private final String status;
    private final String crawlTime;

    public UrlViewModel(int id, String url, String title, String status, String crawlTime) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.status = status;
        this.crawlTime = crawlTime;
    }

    /**
     * 从PageEntity创建视图模型
     */
    public static UrlViewModel fromPageEntity(PageEntity page) {
        return new UrlViewModel(
            page.getId(),
            page.getUrl(),
            page.getTitle(),
            page.getStatus(),
            page.getCrawlTime()
        );
    }

    // ===== Getters =====

    public int getId() { return id; }
    public String getUrl() { return url; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public String getCrawlTime() { return crawlTime; }
}