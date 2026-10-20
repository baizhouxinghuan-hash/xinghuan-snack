package xiaobaisnack.crawler.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

public class LinkExtractor {

    public static Set<String> extractLinks(String html, String baseUrl) {
        Set<String> links = new HashSet<>();

        Document doc = Jsoup.parse(html, baseUrl);
        Elements elements = doc.select("a[href]");

        for (Element el : elements) {
            String url = el.absUrl("href");

            if (url != null && !url.isEmpty()) {
                links.add(normalize(url));
            }
        }

        return links;
    }

    private static String normalize(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }
}