package xiaobaisnack.crawler.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * ⭐ HTTP工具类
 *
 * 功能：
 * - HTTP请求封装
 * - 响应处理
 * - 编码检测
 */
public class HttpUtils {

    private static final int DEFAULT_TIMEOUT = 10000;
    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (compatible; StarMirageCrawler/1.0)";

    public static HttpResponse get(String url) throws IOException {
        return get(url, null, DEFAULT_TIMEOUT);
    }

    public static HttpResponse get(String url, Map<String, String> headers, int timeout) throws IOException {
        HttpURLConnection conn = null;
        try {
            URL urlObj = URI.create(url).toURL();
            conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("User-Agent", DEFAULT_USER_AGENT);
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            int statusCode = conn.getResponseCode();
            String contentType = conn.getContentType();
            String contentEncoding = conn.getContentEncoding();
            Map<String, String> responseHeaders = new HashMap<>();
            conn.getHeaderFields().forEach((k, v) -> {
                if (k != null && v != null && !v.isEmpty()) {
                    responseHeaders.put(k, v.get(0));
                }
            });

            byte[] body = readResponseBody(conn, contentEncoding);
            String charset = detectCharset(contentType, body);
            return new HttpResponse(statusCode, new String(body, charset), contentType, responseHeaders);

        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private static byte[] readResponseBody(HttpURLConnection conn, String contentEncoding) throws IOException {
        InputStream inputStream;
        try {
            inputStream = conn.getInputStream();
        } catch (IOException e) {
            inputStream = conn.getErrorStream();
            if (inputStream == null) throw e;
        }

        if ("gzip".equalsIgnoreCase(contentEncoding)) {
            inputStream = new GZIPInputStream(inputStream);
        } else if ("deflate".equalsIgnoreCase(contentEncoding)) {
            inputStream = new InflaterInputStream(inputStream);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        inputStream.close();
        return baos.toByteArray();
    }

    private static String detectCharset(String contentType, byte[] body) {
        if (contentType != null) {
            String lower = contentType.toLowerCase();
            int charsetIndex = lower.indexOf("charset=");
            if (charsetIndex >= 0) {
                String charset = lower.substring(charsetIndex + 8).trim();
                if (charset.startsWith("\"") && charset.endsWith("\"")) {
                    charset = charset.substring(1, charset.length() - 1);
                }
                return safeCharset(charset);
            }
        }

        String html = new String(body, StandardCharsets.ISO_8859_1);
        String lower = html.toLowerCase();
        int metaIndex = lower.indexOf("charset=");
        if (metaIndex >= 0) {
            String charset = lower.substring(metaIndex + 8);
            int endIndex = charset.indexOf('"');
            if (endIndex > 0) charset = charset.substring(0, endIndex);
            else {
                endIndex = charset.indexOf('>');
                if (endIndex > 0) charset = charset.substring(0, endIndex);
            }
            charset = charset.trim();
            if (!charset.isEmpty()) return safeCharset(charset);
        }
        return "UTF-8";
    }

    /**
     * 校验编码名是否受 JVM 支持，不支持时回退 UTF-8。
     */
    private static String safeCharset(String charset) {
        try {
            return Charset.isSupported(charset) ? charset : "UTF-8";
        } catch (Exception e) {
            return "UTF-8";
        }
    }
    public static boolean isReachable(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            int code = conn.getResponseCode();
            conn.disconnect();
            return code >= 200 && code < 400;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getDomain(String url) {
        try {
            String temp = url.replace("http://", "").replace("https://", "");
            int slashIndex = temp.indexOf('/');
            return slashIndex > 0 ? temp.substring(0, slashIndex) : temp;
        } catch (Exception e) {
            return "";
        }
    }

    public static class HttpResponse {
        public final int statusCode;
        public final String body;
        public final String contentType;
        public final Map<String, String> headers;

        public HttpResponse(int statusCode, String body, String contentType, Map<String, String> headers) {
            this.statusCode = statusCode;
            this.body = body;
            this.contentType = contentType;
            this.headers = headers;
        }

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }

        public boolean isRedirect() {
            return statusCode >= 300 && statusCode < 400;
        }

        @Override
        public String toString() {
            return "HttpResponse{status=" + statusCode + ", contentType=" + contentType + ", bodyLength=" + (body != null ? body.length() : 0) + "}";
        }
    }
}