package xiaobaisnack.crawler.search.analyzer;

import java.util.ArrayList;
import java.util.List;

/**
 * ⭐ 分词器
 *
 * 功能：
 * - 将文本切分为词语
 * - 支持中英文混合分词
 * - 简单实现：英文按空格分词，中文按字符分词
 */
public class Tokenizer {

    private final StopWords stopWords;

    public Tokenizer() {
        this.stopWords = new StopWords();
    }

    public Tokenizer(StopWords stopWords) {
        this.stopWords = stopWords;
    }

    /**
     * 分词
     * @param text 待分词文本
     * @return 词语列表（已过滤停用词）
     */
    public List<String> tokenize(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> tokens = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (Character.isLetterOrDigit(c)) {
                currentWord.append(Character.toLowerCase(c));
            } else if (isChineseCharacter(c)) {
                // 先保存之前的英文词
                if (currentWord.length() > 0) {
                    addToken(tokens, currentWord.toString());
                    currentWord.setLength(0);
                }
                // 中文字符单独作为一个词
                String chineseWord = String.valueOf(c);
                addToken(tokens, chineseWord);
            } else {
                // 分隔符：保存当前词
                if (currentWord.length() > 0) {
                    addToken(tokens, currentWord.toString());
                    currentWord.setLength(0);
                }
            }
        }

        // 保存最后一个词
        if (currentWord.length() > 0) {
            addToken(tokens, currentWord.toString());
        }

        return tokens;
    }

    /**
     * 添加词语（过滤停用词和短词）
     */
    private void addToken(List<String> tokens, String word) {
        if (word.length() < 2) return;  // 过滤单字符
        if (stopWords.isStopWord(word)) return;  // 过滤停用词
        tokens.add(word);
    }

    /**
     * 判断是否为中文字符
     */
    private boolean isChineseCharacter(char c) {
        return Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN;
    }

    /**
     * 获取停用词表
     */
    public StopWords getStopWords() {
        return stopWords;
    }
}