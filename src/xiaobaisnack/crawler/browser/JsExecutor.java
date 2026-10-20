package xiaobaisnack.crawler.browser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ⭐ JS执行器（简化版）
 *
 * 功能：
 * - 模拟执行页面中的JS逻辑
 * - 提取JS动态生成的内容
 * - 处理简单的DOM操作
 *
 * 注意：完整JS执行需要Rhino/Nashorn等引擎
 * 本实现为轻量级模拟，通过正则提取JS中的数据
 */
public class JsExecutor {

    /**
     * 执行页面中的脚本，提取动态内容
     * @param html 原始HTML
     * @return 处理后的HTML
     */
    public String executeScripts(String html) {
        if (html == null || html.isEmpty()) return html;

        String result = html;

        // 提取document.write内容
        result = extractDocumentWrite(result);

        // 提取innerHTML设置
        result = extractInnerHTML(result);

        // 提取JSON数据
        result = extractJsonData(result);

        // 移除script标签（已处理）
        result = removeScriptTags(result);

        return result;
    }

    /**
     * 提取document.write内容
     */
    private String extractDocumentWrite(String html) {
        Pattern pattern = Pattern.compile(
            "document\\.write\\s*\\(\\s*['\"](.+?)['\"]\\s*\\)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        Matcher matcher = pattern.matcher(html);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            sb.append(matcher.group(1)).append(" ");
        }
        return html + "\n<!-- JS Generated -->\n" + sb.toString();
    }

    /**
     * 提取innerHTML设置
     */
    private String extractInnerHTML(String html) {
        Pattern pattern = Pattern.compile(
            "\\.innerHTML\\s*=\\s*['\"](.+?)['\"]",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        Matcher matcher = pattern.matcher(html);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            sb.append(matcher.group(1)).append(" ");
        }
        return html + "\n" + sb.toString();
    }

    /**
     * 提取JSON数据（常用于动态渲染）
     */
    private String extractJsonData(String html) {
        StringBuilder sb = new StringBuilder();

        // 提取 window.__INITIAL_STATE__ 等常见变量
        Pattern pattern = Pattern.compile(
            "window\\.__(\\w+)__\\s*=\\s*(\\{.+?\\});",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            String varName = matcher.group(1);
            String jsonData = matcher.group(2);
            // 提取JSON中的文本内容
            extractTextFromJson(jsonData, sb);
        }

        return html + "\n<!-- JSON Data -->\n" + sb.toString();
    }

    /**
     * 从JSON中提取文本
     */
    private void extractTextFromJson(String json, StringBuilder sb) {
        // 提取 "title": "..." 和 "content": "..." 等
        Pattern textPattern = Pattern.compile(
            "\"(title|content|text|name|description|summary)\"\\s*:\\s*\"(.+?)\"",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = textPattern.matcher(json);
        while (matcher.find()) {
            sb.append(matcher.group(2)).append(" ");
        }
    }

    /**
     * 移除script标签
     */
    private String removeScriptTags(String html) {
        return html.replaceAll("<script[^>]*>.*?</script>", "");
    }

    /**
     * 执行简单的JS表达式（非常有限）
     */
    public String evaluateExpression(String expression) {
        if (expression == null || expression.isEmpty()) return "";

        // 处理简单的字符串拼接
        if (expression.contains("+")) {
            String[] parts = expression.split("\\+");
            StringBuilder sb = new StringBuilder();
            for (String part : parts) {
                part = part.trim();
                if (part.startsWith("\"") && part.endsWith("\"")) {
                    sb.append(part.substring(1, part.length() - 1));
                } else if (part.startsWith("'") && part.endsWith("'")) {
                    sb.append(part.substring(1, part.length() - 1));
                }
            }
            return sb.toString();
        }

        return expression;
    }
}