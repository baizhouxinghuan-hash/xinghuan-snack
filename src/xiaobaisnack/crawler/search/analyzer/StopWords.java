package xiaobaisnack.crawler.search.analyzer;

import java.util.HashSet;
import java.util.Set;

/**
 * ⭐ 停用词表
 *
 * 功能：
 * - 管理停用词（无实际意义的常见词）
 * - 过滤搜索中的噪音词
 */
public class StopWords {

    private final Set<String> stopWords = new HashSet<>();

    public StopWords() {
        initDefaultStopWords();
    }

    /**
     * 初始化默认停用词
     */
    private void initDefaultStopWords() {
        // 中文停用词
        String[] chineseStopWords = {
            "的", "了", "在", "是", "我", "有", "和", "就", "不", "人",
            "都", "一", "一个", "上", "也", "很", "到", "说", "要", "去",
            "你", "会", "着", "没有", "看", "好", "自己", "这", "那", "他",
            "她", "它", "们", "那", "些", "什么", "怎么", "如何", "为什么",
            "可以", "因为", "所以", "但是", "然而", "虽然", "如果", "的话",
            "已经", "曾经", "还是", "或者", "并且", "而且", "不过", "只是",
            "这个", "那个", "这些", "那些", "这里", "那里", "哪里", "怎么",
            "怎样", "怎么样", "多么", "那么", "这样", "那样", "各种", "每个",
            "任何", "所有", "一些", "许多", "很少", "更多", "最多", "最少",
            "应该", "可能", "可以", "需要", "必须", "能够", "会", "能", "想",
            "知道", "觉得", "认为", "希望", "喜欢", "爱", "让", "把", "被",
            "从", "对", "向", "往", "到", "在", "当", "于", "比", "跟",
            "与", "和", "同", "及", "以及", "或", "并", "而", "且", "但"
        };

        // 英文停用词
        String[] englishStopWords = {
            "the", "a", "an", "is", "are", "was", "were", "be", "been",
            "being", "have", "has", "had", "do", "does", "did", "will",
            "would", "could", "should", "may", "might", "can", "shall",
            "to", "of", "in", "for", "on", "with", "at", "by", "from",
            "as", "into", "through", "during", "before", "after", "above",
            "below", "between", "and", "but", "or", "nor", "not", "so",
            "yet", "both", "either", "neither", "each", "every", "all",
            "any", "few", "more", "most", "other", "some", "such", "no",
            "only", "own", "same", "than", "too", "very", "just", "about",
            "up", "out", "if", "then", "now", "here", "there", "when",
            "where", "why", "how", "which", "who", "whom", "what", "this",
            "that", "these", "those", "i", "me", "my", "myself", "we",
            "our", "ours", "ourselves", "you", "your", "yours", "yourself",
            "yourselves", "he", "him", "his", "himself", "she", "her",
            "hers", "herself", "it", "its", "itself", "they", "them",
            "their", "theirs", "themselves", "am", "is", "are", "was",
            "were", "be", "been", "being", "have", "has", "had", "having",
            "do", "does", "did", "doing", "will", "would", "shall", "should",
            "may", "might", "must", "can", "could", "ought", "need", "dare",
            "used", "get", "got", "go", "went", "gone", "come", "came",
            "make", "made", "know", "knew", "think", "thought", "take",
            "took", "see", "saw", "give", "gave", "find", "found", "tell",
            "told", "ask", "asked", "work", "seem", "feel", "try", "leave",
            "call", "keep", "let", "begin", "show", "hear", "play", "run",
            "move", "live", "believe", "hold", "bring", "happen", "write",
            "provide", "sit", "stand", "lose", "pay", "meet", "include",
            "continue", "set", "learn", "change", "lead", "understand",
            "watch", "follow", "stop", "create", "speak", "read", "allow",
            "add", "spend", "grow", "open", "walk", "win", "offer",
            "remember", "love", "consider", "appear", "buy", "wait",
            "serve", "die", "send", "expect", "build", "stay", "fall",
            "cut", "reach", "kill", "remain", "suggest", "raise", "pass",
            "sell", "require", "report", "decide", "pull", "develop"
        };

        for (String word : chineseStopWords) {
            stopWords.add(word);
        }
        for (String word : englishStopWords) {
            stopWords.add(word);
        }
    }

    /**
     * 判断是否为停用词
     */
    public boolean isStopWord(String word) {
        if (word == null || word.isEmpty()) return true;
        return stopWords.contains(word.toLowerCase());
    }

    /**
     * 添加停用词
     */
    public void addStopWord(String word) {
        if (word != null && !word.isEmpty()) {
            stopWords.add(word.toLowerCase());
        }
    }

    /**
     * 移除停用词
     */
    public void removeStopWord(String word) {
        stopWords.remove(word.toLowerCase());
    }

    /**
     * 获取停用词数量
     */
    public int size() {
        return stopWords.size();
    }

    /**
     * 获取所有停用词
     */
    public Set<String> getAllStopWords() {
        return new HashSet<>(stopWords);
    }
}