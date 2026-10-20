package xiaobaisnack.crawler.cleaner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * ⭐ HTML净化器
 *
 * 功能：
 * - 去除HTML标签、脚本、样式
 * - 提取纯文本内容
 * - 清理空白字符
 */
public class HtmlSanitizer {

    /**
     * 净化HTML，去除所有标签和脚本
     * @param html 原始HTML
     * @return 纯文本内容
     */
    public static String sanitize(String html) {
        if (html == null || html.isEmpty()) return "";

        Document doc = Jsoup.parse(html);

        // 移除不需要的标签
        removeUnwantedElements(doc);

        // 获取body文本
        Element body = doc.body();
        if (body == null) return "";

        String text = body.text();

        // 清理多余空白
        text = cleanWhitespace(text);

        return text;
    }

    /**
     * 移除不需要的HTML元素
     */
    private static void removeUnwantedElements(Document doc) {
        // 脚本和样式
        doc.select("script, style, noscript, iframe, embed, object, applet").remove();

        // 注释
        doc.select("*").forEach(el -> {
            el.html(el.html().replaceAll("<!--.*?-->", ""));
        });

        // 隐藏元素
        doc.select("[style*='display:none'], [style*='display: none']").remove();
        doc.select("[hidden], [aria-hidden='true']").remove();

        // 表单元素
        doc.select("form, input, textarea, select, button").remove();

        // 导航元素
        doc.select("nav, header, footer, aside").remove();

        // 常见广告类
        Elements ads = doc.select(
            "[class*=ad], [id*=ad], " +
            "[class*=banner], [id*=banner], " +
            "[class*=popup], [id*=popup], " +
            "[class*=sponsor], [id*=sponsor], " +
            "[class*=sidebar], [id*=sidebar], " +
            "[class*=widget], [id*=widget], " +
            "[class*=social], [id*=social], " +
            "[class*=share], [id*=share], " +
            "[class*=comment], [id*=comment], " +
            "[class*=related], [id*=related], " +
            "[class*=recommend], [id*=recommend]"
        );
        for (Element ad : ads) {
            ad.remove();
        }
    }

    /**
     * 清理多余空白字符
     */
    private static String cleanWhitespace(String text) {
        if (text == null) return "";

        // 将多个空白字符替换为单个空格
        text = text.replaceAll("\\s+", " ");

        // 去除首尾空白
        text = text.trim();

        return text;
    }

    /**
     * 提取指定CSS选择器的内容
     * @param html 原始HTML
     * @param cssSelector CSS选择器
     * @return 提取的文本
     */
    public static String extractBySelector(String html, String cssSelector) {
        if (html == null || html.isEmpty()) return "";

        Document doc = Jsoup.parse(html);
        Elements elements = doc.select(cssSelector);

        if (elements.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (Element el : elements) {
            sb.append(el.text()).append(" ");
        }

        return cleanWhitespace(sb.toString());
    }

    /**
     * 提取页面元描述
     */
    public static String extractMetaDescription(String html) {
        if (html == null || html.isEmpty()) return "";

        Document doc = Jsoup.parse(html);
        Element meta = doc.selectFirst("meta[name=description]");
        if (meta != null) {
            return meta.attr("content");
        }

        meta = doc.selectFirst("meta[property='og:description']");
        if (meta != null) {
            return meta.attr("content");
        }

        return "";
    }

    /**
     * 提取页面关键词
     */
    public static String extractMetaKeywords(String html) {
        if (html == null || html.isEmpty()) return "";

        Document doc = Jsoup.parse(html);
        Element meta = doc.selectFirst("meta[name=keywords]");
        if (meta != null) {
            return meta.attr("content");
        }

        return "";
    }
}