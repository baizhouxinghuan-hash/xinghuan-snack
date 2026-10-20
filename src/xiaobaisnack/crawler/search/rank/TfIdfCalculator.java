package xiaobaisnack.crawler.search.rank;

import java.util.HashMap;
import java.util.Map;

/**
 * ⭐ TF-IDF计算器
 *
 * 功能：
 * - 计算词频(TF)和逆文档频率(IDF)
 * - 计算TF-IDF权重
 * - 用于搜索结果排序
 *
 * 公式：
 * TF = 词在文档中出现次数 / 文档总词数
 * IDF = log(总文档数 / (包含该词的文档数 + 1))
 * TF-IDF = TF * IDF
 */
public class TfIdfCalculator {

    /**
     * 计算TF（词频）
     * @param termFrequency 词在文档中出现的次数
     * @param totalTerms 文档总词数
     * @return TF值
     */
    public double calculateTf(int termFrequency, int totalTerms) {
        if (totalTerms == 0) return 0;
        return (double) termFrequency / totalTerms;
    }

    /**
     * 计算IDF（逆文档频率）
     * @param totalDocs 总文档数
     * @param docFrequency 包含该词的文档数
     * @return IDF值
     */
    public double calculateIdf(int totalDocs, int docFrequency) {
        if (docFrequency == 0) return 0;
        return Math.log((double) (totalDocs + 1) / (docFrequency + 1));
    }

    /**
     * 计算TF-IDF
     * @param tf 词频
     * @param idf 逆文档频率
     * @return TF-IDF值
     */
    public double calculateTfIdf(double tf, double idf) {
        return tf * idf;
    }

    /**
     * 计算TF-IDF（一步到位）
     * @param termFrequency 词频
     * @param totalTerms 文档总词数
     * @param totalDocs 总文档数
     * @param docFrequency 包含该词的文档数
     * @return TF-IDF值
     */
    public double calculateTfIdf(int termFrequency, int totalTerms, int totalDocs, int docFrequency) {
        double tf = calculateTf(termFrequency, totalTerms);
        double idf = calculateIdf(totalDocs, docFrequency);
        return calculateTfIdf(tf, idf);
    }

    /**
     * 批量计算文档的TF-IDF分数
     * @param docTermFreqs 每个文档的词频映射 (docId -> (term -> frequency))
     * @param totalDocs 总文档数
     * @return 每个文档的TF-IDF总分 (docId -> score)
     */
    public Map<Integer, Double> calculateBatchTfIdf(
            Map<Integer, Map<String, Integer>> docTermFreqs,
            int totalDocs) {

        Map<Integer, Double> scores = new HashMap<>();

        // 计算每个词的文档频率
        Map<String, Integer> docFrequency = new HashMap<>();
        for (Map<String, Integer> termFreqs : docTermFreqs.values()) {
            for (String term : termFreqs.keySet()) {
                docFrequency.put(term, docFrequency.getOrDefault(term, 0) + 1);
            }
        }

        // 计算每个文档的TF-IDF总分
        for (Map.Entry<Integer, Map<String, Integer>> docEntry : docTermFreqs.entrySet()) {
            int docId = docEntry.getKey();
            Map<String, Integer> termFreqs = docEntry.getValue();

            // 计算文档总词数
            int totalTerms = termFreqs.values().stream().mapToInt(Integer::intValue).sum();

            double totalScore = 0;
            for (Map.Entry<String, Integer> termEntry : termFreqs.entrySet()) {
                String term = termEntry.getKey();
                int tf = termEntry.getValue();
                int df = docFrequency.getOrDefault(term, 0);

                double score = calculateTfIdf(tf, totalTerms, totalDocs, df);
                totalScore += score;
            }

            scores.put(docId, totalScore);
        }

        return scores;
    }

    /**
     * 计算余弦相似度
     * @param vec1 向量1
     * @param vec2 向量2
     * @return 余弦相似度 (0-1)
     */
    public double cosineSimilarity(Map<String, Double> vec1, Map<String, Double> vec2) {
        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;

        for (Map.Entry<String, Double> entry : vec1.entrySet()) {
            String key = entry.getKey();
            double val1 = entry.getValue();
            double val2 = vec2.getOrDefault(key, 0.0);

            dotProduct += val1 * val2;
            norm1 += val1 * val1;
        }

        for (double val : vec2.values()) {
            norm2 += val * val;
        }

        if (norm1 == 0 || norm2 == 0) return 0;

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}