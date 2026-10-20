package xiaobaisnack.crawler.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class PageParser {

    /**
     * 解析 HTML 页面，提取标题和正文
     * @param html 网页 HTML
     * @return PageData 包含标题和正文
     */
    public PageData parse(String html) {
        if (html == null || html.isEmpty()) return null;

        Document doc = Jsoup.parse(html);

        // 提取标题
        String title = doc.title();

        // 尝试提取正文
        Element body = doc.body();
        String content = (body != null) ? body.text() : "";

        return new PageData(title, content);
    }

    // 简单封装
    public static class PageData {
        public final String title;
        public final String content;

        public PageData(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }
}