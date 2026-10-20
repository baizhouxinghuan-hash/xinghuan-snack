package xiaobaisnack.crawler.cleaner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ⭐ 规则引擎
 *
 * 功能：
 * - 过滤低质量内容
 * - 基于规则判断内容是否有效
 * - 支持自定义规则扩展
 */
public class RuleEngine {

    private final List<Rule> rules = new ArrayList<>();

    public RuleEngine() {
        // 初始化默认规则
        initDefaultRules();
    }

    /**
     * 初始化默认过滤规则
     */
    private void initDefaultRules() {
        // 规则1：内容太短（少于50字符）
        rules.add(new Rule("min-length", content -> {
            if (content == null || content.trim().length() < 50) {
                return RuleResult.reject("内容过短（少于50字符）");
            }
            return RuleResult.accept();
        }));

        // 规则2：内容太长（超过100万字符，可能是垃圾）
        rules.add(new Rule("max-length", content -> {
            if (content != null && content.length() > 1_000_000) {
                return RuleResult.reject("内容过长（超过100万字符）");
            }
            return RuleResult.accept();
        }));

        // 规则3：检测纯数字/符号内容
        rules.add(new Rule("no-meaningful-text", content -> {
            if (content != null) {
                String text = content.replaceAll("[\\s\\p{Punct}\\d]", "");
                if (text.length() < 20) {
                    return RuleResult.reject("有效文本不足");
                }
            }
            return RuleResult.accept();
        }));

        // 规则4：检测重复字符过多
        rules.add(new Rule("repetitive", content -> {
            if (content != null && content.length() > 100) {
                // 检测是否有连续重复超过20次的字符
                if (Pattern.compile("(.)\\1{20,}").matcher(content).find()) {
                    return RuleResult.reject("包含大量重复字符");
                }
            }
            return RuleResult.accept();
        }));

        // 规则5：检测垃圾关键词
        rules.add(new Rule("spam-keywords", content -> {
            if (content != null) {
                String lower = content.toLowerCase();
                String[] spamWords = {
                    "buy now", "click here", "free money", "casino",
                    "lottery", "winner", "congratulations", "you won",
                    "limited offer", "act now", "100% free",
                    "cheap", "discount", "best price", "lowest price"
                };
                int spamCount = 0;
                for (String word : spamWords) {
                    if (lower.contains(word)) {
                        spamCount++;
                    }
                }
                if (spamCount >= 5) {
                    return RuleResult.reject("包含大量垃圾关键词");
                }
            }
            return RuleResult.accept();
        }));

        // 规则6：检测中文内容比例
        rules.add(new Rule("chinese-ratio", content -> {
            if (content != null && content.length() > 100) {
                int chineseCount = 0;
                for (char c : content.toCharArray()) {
                    if (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN) {
                        chineseCount++;
                    }
                }
                // 如果中文占比超过80%，可能是纯中文页面（正常）
                // 如果中文占比低于5%且内容很长，可能是乱码
                double ratio = (double) chineseCount / content.length();
                if (content.length() > 500 && ratio < 0.02 && chineseCount < 5) {
                    return RuleResult.reject("中文内容比例过低，可能为乱码");
                }
            }
            return RuleResult.accept();
        }));
    }

    /**
     * 添加自定义规则
     */
    public void addRule(Rule rule) {
        rules.add(rule);
    }

    /**
     * 移除规则
     */
    public void removeRule(String ruleName) {
        rules.removeIf(r -> r.name.equals(ruleName));
    }

    /**
     * 评估内容，返回是否通过所有规则
     * @param content 待评估的内容
     * @return 评估结果
     */
    public RuleResult evaluate(String content) {
        for (Rule rule : rules) {
            RuleResult result = rule.evaluator.evaluate(content);
            if (!result.passed) {
                return result;
            }
        }
        return RuleResult.accept();
    }

    /**
     * 评估内容，返回所有规则的评估结果
     */
    public List<RuleResult> evaluateAll(String content) {
        List<RuleResult> results = new ArrayList<>();
        for (Rule rule : rules) {
            results.add(rule.evaluator.evaluate(content));
        }
        return results;
    }

    /**
     * 规则定义
     */
    public static class Rule {
        public final String name;
        public final RuleEvaluator evaluator;

        public Rule(String name, RuleEvaluator evaluator) {
            this.name = name;
            this.evaluator = evaluator;
        }
    }

    /**
     * 规则评估器接口
     */
    @FunctionalInterface
    public interface RuleEvaluator {
        RuleResult evaluate(String content);
    }

    /**
     * 规则评估结果
     */
    public static class RuleResult {
        public final boolean passed;
        public final String reason;

        private RuleResult(boolean passed, String reason) {
            this.passed = passed;
            this.reason = reason;
        }

        public static RuleResult accept() {
            return new RuleResult(true, "通过");
        }

        public static RuleResult reject(String reason) {
            return new RuleResult(false, reason);
        }

        @Override
        public String toString() {
            return passed ? "✓ 通过" : "✗ 拒绝: " + reason;
        }
    }
}