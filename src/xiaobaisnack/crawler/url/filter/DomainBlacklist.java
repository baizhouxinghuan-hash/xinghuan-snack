package xiaobaisnack.crawler.url.filter;

import java.util.HashSet;
import java.util.Set;

public class DomainBlacklist {

    private static final Set<String> BLOCKED_DOMAINS = new HashSet<>();

    static {
        // 🚫 示例（你可以随时加）
        BLOCKED_DOMAINS.add("example.com");
        BLOCKED_DOMAINS.add("doubleclick.net");
        BLOCKED_DOMAINS.add("ads.google.com");
    }

    public static boolean isBlocked(String url) {
        String domain = extractDomain(url);
        return BLOCKED_DOMAINS.contains(domain);
    }

    private static String extractDomain(String url) {
        try {
            String temp = url.replace("http://", "")
                             .replace("https://", "");
            return temp.split("/")[0];
        } catch (Exception e) {
            return "";
        }
    }
}