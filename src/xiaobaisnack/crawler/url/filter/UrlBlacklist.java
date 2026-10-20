package xiaobaisnack.crawler.url.filter;

public class UrlBlacklist {

    public static boolean isBlocked(String url) {
        if (url == null || url.isEmpty()) return true;

        // 🚫 非HTTP协议
        if (!url.startsWith("http")) return true;

        // 🚫 静态资源
        String lower = url.toLowerCase();
        if (lower.matches(".*\\.(jpg|jpeg|png|gif|css|js|ico|svg|pdf|zip|rar)$")) {
            return true;
        }

        // 🚫 常见无用路径
        if (lower.contains("login") ||
            lower.contains("logout") ||
            lower.contains("register") ||
            lower.contains("signup") ||
            lower.contains("cart") ||
            lower.contains("payment")) {
            return true;
        }

        return false;
    }
}