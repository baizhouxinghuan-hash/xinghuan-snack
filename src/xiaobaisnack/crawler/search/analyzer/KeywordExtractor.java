package xiaobaisnack.crawler.search.analyzer;

import java.util.*;

/**
 * ⭐ 关键词提取器
 *
 * 功能：
 * - 从文本中提取关键词
 * - 基于词频统计
 * - 支持TF-IDF权重计算
 */
public class KeywordExtractor {

    private final Tokenizer tokenizer;
    private final int maxKeywords;

    public KeywordExtractor() {
        this(new Tokenizer(), 20);
    }

    public KeywordExtractor(Tokenizer tokenizer, int maxKeywords) {
        this.tokenizer = tokenizer;
        this.maxKeywords = maxKeywords;
    }

    /**
     * 提取关键词
     * @param text 文本内容
     * @return 关键词及其权重
     */
    public Map<String, Double> extract(String text) {
        if (text == null || text.isEmpty()) {
            return new LinkedHashMap<>();
        }

        // 分词
        List<String> tokens = tokenizer.tokenize(text);

        // 统计词频
        Map<String, Integer> wordFreq = new HashMap<>();
        for (String token : tokens) {
            wordFreq.put(token, wordFreq.getOrDefault(token, 0) + 1);
        }

        // 计算总词数
        int totalWords = tokens.size();
        if (totalWords == 0) return new LinkedHashMap<>();

        // 计算TF值并排序
        Map<String, Double> tfMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : wordFreq.entrySet()) {
            double tf = (double) entry.getValue() / totalWords;
            tfMap.put(entry.getKey(), tf);
        }

        // 按TF值排序，取前N个
        List<Map.Entry<String, Double>> sorted = new ArrayList<>(tfMap.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        Map<String, Double> result = new LinkedHashMap<>();
        int count = 0;
        for (Map.Entry<String, Double> entry : sorted) {
            if (count >= maxKeywords) break;
            result.put(entry.getKey(), entry.getValue());
            count++;
        }

        return result;
    }

    /**
     * 提取关键词列表（不含权重）
     */
    public List<String> extractKeywords(String text) {
        return new ArrayList<>(extract(text).keySet());
    }

    /**
     * 提取关键词列表（含权重，按权重排序）
     */
    public List<Map.Entry<String, Double>> extractWithWeights(String text) {
        return new ArrayList<>(extract(text).entrySet());
    }

    /**
     * 获取分词器
     */
    public Tokenizer getTokenizer() {
        return tokenizer;
    }
}