package xiaobaisnack.crawler.url.manager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

/**
 * URL 规范化工具。
 *
 * <p>统一 scheme/host 大小写、去掉默认端口、去掉 fragment、去掉路径末尾的斜杠，
 * 但保留查询串，避免 /article?id=1 与 /article?id=2 被错误判重。</p>
 */
public final class UrlNormalizer {

    private UrlNormalizer() {
    }

    public static String normalize(String url) {
        if (url == null) {
            return null;
        }

        String trimmed = url.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        try {
            URI uri = new URI(trimmed);
            String scheme = uri.getScheme();
            if (scheme == null) {
                return null;
            }
            scheme = scheme.toLowerCase(Locale.ROOT);
            if (!"http".equals(scheme) && !"https".equals(scheme)) {
                return null;
            }

            String host = uri.getHost();
            if (host == null || host.isEmpty()) {
                return null;
            }
            host = host.toLowerCase(Locale.ROOT);

            int port = uri.getPort();
            if ((port == 80 && "http".equals(scheme)) || (port == 443 && "https".equals(scheme))) {
                port = -1;
            }

            String path = uri.getPath() == null ? "" : uri.getPath();
            if ("/".equals(path)) {
                path = "";
            } else if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }

            StringBuilder sb = new StringBuilder();
            sb.append(scheme).append("://").append(host);
            if (port != -1) {
                sb.append(':').append(port);
            }
            sb.append(path);

            String query = uri.getQuery();
            if (query != null && !query.isEmpty()) {
                sb.append('?').append(query);
            }
            return sb.toString();

        } catch (URISyntaxException e) {
            return null;
        }
    }
}