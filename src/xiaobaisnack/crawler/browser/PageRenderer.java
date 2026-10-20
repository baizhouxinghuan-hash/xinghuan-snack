package xiaobaisnack.crawler.browser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * ⭐ 页面渲染器
 *
 * 功能：
 * - 渲染HTML页面
 * - 提取可视内容
 * - 处理页面布局
 */
public class PageRenderer {

    /**
     * 渲染页面，提取主要内容
     * @param html 原始HTML
     * @return 渲染后的文本内容
     */
    public String render(String html) {
        if (html == null || html.isEmpty()) return "";

        Document doc = Jsoup.parse(html);

        // 移除不可见元素
        removeInvisibleElements(doc);

        // 提取主要内容区域
        Element mainContent = extractMainContent(doc);

        if (mainContent != null) {
            return mainContent.text();
        }

        // 回退：使用body
        Element body = doc.body();
        return body != null ? body.text() : "";
    }

    /**
     * 移除不可见元素
     */
    private void removeInvisibleElements(Document doc) {
        // 脚本和样式
        doc.select("script, style, noscript, iframe, svg, canvas").remove();

        // 隐藏元素
        doc.select("[hidden], [aria-hidden='true']").remove();
        doc.select("[style*='display:none'], [style*='display: none']").remove();
        doc.select("[style*='visibility:hidden'], [style*='visibility: hidden']").remove();

        // 导航和页脚
        doc.select("nav, header, footer, aside").remove();
    }

    /**
     * 提取主要内容区域
     */
    private Element extractMainContent(Document doc) {
        // 尝试常见的内容容器
        String[] contentSelectors = {
            "article", "main", "[role='main']",
            ".content", ".article", ".post", ".entry",
            "#content", "#article", "#main", "#post",
            ".post-content", ".article-content", ".entry-content",
            ".markdown-body", ".rich_media_content"
        };

        for (String selector : contentSelectors) {
            Elements elements = doc.select(selector);
            if (!elements.isEmpty()) {
                // 选择文本最多的元素
                Element best = elements.first();
                int maxTextLength = best.text().length();
                for (Element el : elements) {
                    int len = el.text().length();
                    if (len > maxTextLength) {
                        maxTextLength = len;
                        best = el;
                    }
                }
                if (maxTextLength > 100) {
                    return best;
                }
            }
        }

        return null;
    }

    /**
     * 提取页面标题
     */
    public String extractTitle(String html) {
        if (html == null || html.isEmpty()) return "";
        Document doc = Jsoup.parse(html);
        return doc.title();
    }

    /**
     * 提取所有链接
     */
    public Elements extractLinks(String html) {
        if (html == null || html.isEmpty()) return new Elements();
        Document doc = Jsoup.parse(html);
        return doc.select("a[href]");
    }

    /**
     * 提取所有图片
     */
    public Elements extractImages(String html) {
        if (html == null || html.isEmpty()) return new Elements();
        Document doc = Jsoup.parse(html);
        return doc.select("img[src]");
    }
}