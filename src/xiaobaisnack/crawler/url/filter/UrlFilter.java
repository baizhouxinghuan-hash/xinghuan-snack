package xiaobaisnack.crawler.url.filter;

public class UrlFilter {

    public boolean allow(String url) {

        // 1️⃣ 基础黑名单
        if (UrlBlacklist.isBlocked(url)) {
            return false;
        }

        // 2️⃣ 域名黑名单
        if (DomainBlacklist.isBlocked(url)) {
            return false;
        }

        // 3️⃣ 正则规则
        if (PatternBlacklist.isBlocked(url)) {
            return false;
        }

        return true;
    }
}