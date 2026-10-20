package xiaobaisnack.crawler.core;

import xiaobaisnack.crawler.utils.HttpUtils;

import java.io.IOException;

/**
 * 页面下载器。
 *
 * <p>复用 HttpUtils：状态码校验、gzip/deflate 解压、
 * 基于 Content-Type 与 meta 的编码识别，避免中文站点乱码。</p>
 */
public class PageFetcher {

    private static final int MAX_BODY_CHARS = 2_000_000;

    public String fetch(String urlStr) {
        if (urlStr == null || urlStr.trim().isEmpty()) {
            return null;
        }

        try {
            HttpUtils.HttpResponse response = HttpUtils.get(urlStr.trim());
            if (!response.isSuccess()) {
                System.err.println("下载失败: " + urlStr + " HTTP " + response.statusCode);
                return null;
            }

            String contentType = response.contentType;
            if (contentType != null) {
                String lower = contentType.toLowerCase();
                if (!lower.contains("html") && !lower.contains("xml") && !lower.contains("text")) {
                    System.err.println("跳过非文本资源: " + urlStr + " (" + contentType + ")");
                    return null;
                }
            }

            String body = response.body;
            if (body == null || body.isEmpty()) {
                return null;
            }
            return body.length() > MAX_BODY_CHARS ? body.substring(0, MAX_BODY_CHARS) : body;

        } catch (IOException | IllegalArgumentException e) {
            System.err.println("下载失败: " + urlStr + " 原因: " + e.getMessage());
            return null;
        }
    }
}