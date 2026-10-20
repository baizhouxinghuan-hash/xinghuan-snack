package xiaobaisnack.crawler.cleaner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ContentCleaner {

    /**
     * 清理 HTML，去掉广告、导航、脚本、样式
     * 返回干净正文
     */
    public static String clean(String html) {
        if (html == null || html.isEmpty()) return "";

        Document doc = Jsoup.parse(html);

        // 移除 script、style 标签
        doc.select("script, style, nav, header, footer, aside, noscript").remove();

        // 移除广告类元素（可根据类名或id扩展）
        Elements ads = doc.select("[class*=ad], [id*=ad]");
        for (Element ad : ads) ad.remove();

        // 返回干净正文
        Element body = doc.body();
        return body != null ? body.text() : "";
    }
}