package xiaobaisnack.crawler.browser;

import xiaobaisnack.crawler.core.PageFetcher;
import xiaobaisnack.crawler.core.PageParser;
import xiaobaisnack.crawler.cleaner.ContentCleaner;

/**
 * ⭐ 模拟浏览器引擎
 *
 * 功能：
 * - 模拟浏览器行为抓取网页
 * - 处理JS动态渲染页面（简化版）
 * - 管理请求头和Cookie
 *
 * 注意：完整JS渲染需要Selenium/Playwright等工具
 * 本实现为轻量级模拟，通过设置浏览器请求头来模拟
 */
public class BrowserEngine {

    private final PageFetcher fetcher;
    private final PageRenderer renderer;
    private final JsExecutor jsExecutor;
    private String userAgent;
    private boolean javascriptEnabled = true;
    private boolean loadImages = false;

    public BrowserEngine() {
        this.fetcher = new PageFetcher();
        this.renderer = new PageRenderer();
        this.jsExecutor = new JsExecutor();
        this.userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    }

    /**
     * 模拟浏览器打开页面
     * @param url 目标URL
     * @return 渲染后的页面内容
     */
    public BrowserPage openPage(String url) {
        System.out.println("[BrowserEngine] 打开页面: " + url);

        // 1. 抓取HTML
        String html = fetcher.fetch(url);
        if (html == null) {
            return new BrowserPage(url, "", "抓取失败");
        }

        // 2. 解析页面
        PageParser.PageData pageData = new PageParser().parse(html);

        // 3. 执行JS（简化：提取JS生成的内容）
        if (javascriptEnabled) {
            html = jsExecutor.executeScripts(html);
        }

        // 4. 渲染页面
        String renderedContent = renderer.render(html);

        // 5. 清洗内容
        String cleanContent = ContentCleaner.clean(html);

        return new BrowserPage(url, pageData.title, cleanContent);
    }

    /**
     * 搜索模式：模拟浏览器搜索
     * @param keyword 搜索关键词
     * @param searchEngine 搜索引擎（baidu/google）
     * @return 搜索结果
     */
    public String search(String keyword, String searchEngine) {
        String searchUrl = buildSearchUrl(keyword, searchEngine);
        System.out.println("[BrowserEngine] 搜索: " + keyword + " 引擎: " + searchEngine);

        String html = fetcher.fetch(searchUrl);
        if (html == null) return "";

        return ContentCleaner.clean(html);
    }

    /**
     * 构建搜索URL
     */
    private String buildSearchUrl(String keyword, String searchEngine) {
        try {
            String encoded = java.net.URLEncoder.encode(keyword, "UTF-8");
            switch (searchEngine.toLowerCase()) {
                case "baidu":
                    return "https://www.baidu.com/s?wd=" + encoded;
                case "google":
                    return "https://www.google.com/search?q=" + encoded;
                case "bing":
                    return "https://www.bing.com/search?q=" + encoded;
                default:
                    return "https://www.baidu.com/s?wd=" + encoded;
            }
        } catch (Exception e) {
            return "https://www.baidu.com/s?wd=" + keyword;
        }
    }

    // ===== Getters & Setters =====

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public boolean isJavascriptEnabled() {
        return javascriptEnabled;
    }

    public void setJavascriptEnabled(boolean javascriptEnabled) {
        this.javascriptEnabled = javascriptEnabled;
    }

    public boolean isLoadImages() {
        return loadImages;
    }

    public void setLoadImages(boolean loadImages) {
        this.loadImages = loadImages;
    }

    /**
     * 浏览器页面结果
     */
    public static class BrowserPage {
        public final String url;
        public final String title;
        public final String content;

        public BrowserPage(String url, String title, String content) {
            this.url = url;
            this.title = title;
            this.content = content;
        }

        @Override
        public String toString() {
            return "BrowserPage{url='" + url + "', title='" + title + "'}";
        }
    }
}