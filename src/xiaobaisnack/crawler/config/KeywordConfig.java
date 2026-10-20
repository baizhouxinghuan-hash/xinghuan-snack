package xiaobaisnack.crawler.config;

import java.util.ArrayList;
import java.util.List;

/**
 * ⭐ 搜索关键词配置
 *
 * 功能：
 * - 管理搜索关键词列表
 * - 支持关键词权重配置
 */
public class KeywordConfig {

    private List<String> keywords = new ArrayList<>();
    private List<String> stopWords = new ArrayList<>();
    private int maxKeywords = 10;

    public KeywordConfig() {
        // 默认停用词
        initDefaultStopWords();
    }

    private void initDefaultStopWords() {
        stopWords.add("的");
        stopWords.add("了");
        stopWords.add("在");
        stopWords.add("是");
        stopWords.add("我");
        stopWords.add("有");
        stopWords.add("和");
        stopWords.add("就");
        stopWords.add("不");
        stopWords.add("人");
        stopWords.add("都");
        stopWords.add("一");
        stopWords.add("一个");
        stopWords.add("上");
        stopWords.add("也");
        stopWords.add("很");
        stopWords.add("到");
        stopWords.add("说");
        stopWords.add("要");
        stopWords.add("去");
        stopWords.add("你");
        stopWords.add("会");
        stopWords.add("着");
        stopWords.add("没有");
        stopWords.add("看");
        stopWords.add("好");
        stopWords.add("自己");
        stopWords.add("这");
        stopWords.add("the");
        stopWords.add("a");
        stopWords.add("an");
        stopWords.add("is");
        stopWords.add("are");
        stopWords.add("was");
        stopWords.add("were");
        stopWords.add("be");
        stopWords.add("been");
        stopWords.add("being");
        stopWords.add("have");
        stopWords.add("has");
        stopWords.add("had");
        stopWords.add("do");
        stopWords.add("does");
        stopWords.add("did");
        stopWords.add("will");
        stopWords.add("would");
        stopWords.add("could");
        stopWords.add("should");
        stopWords.add("may");
        stopWords.add("might");
        stopWords.add("can");
        stopWords.add("shall");
        stopWords.add("to");
        stopWords.add("of");
        stopWords.add("in");
        stopWords.add("for");
        stopWords.add("on");
        stopWords.add("with");
        stopWords.add("at");
        stopWords.add("by");
        stopWords.add("from");
        stopWords.add("as");
        stopWords.add("into");
        stopWords.add("through");
        stopWords.add("during");
        stopWords.add("before");
        stopWords.add("after");
        stopWords.add("above");
        stopWords.add("below");
        stopWords.add("between");
        stopWords.add("and");
        stopWords.add("but");
        stopWords.add("or");
        stopWords.add("nor");
        stopWords.add("not");
        stopWords.add("so");
        stopWords.add("yet");
        stopWords.add("both");
        stopWords.add("either");
        stopWords.add("neither");
        stopWords.add("each");
        stopWords.add("every");
        stopWords.add("all");
        stopWords.add("any");
        stopWords.add("few");
        stopWords.add("more");
        stopWords.add("most");
        stopWords.add("other");
        stopWords.add("some");
        stopWords.add("such");
        stopWords.add("no");
        stopWords.add("only");
        stopWords.add("own");
        stopWords.add("same");
        stopWords.add("than");
        stopWords.add("too");
        stopWords.add("very");
        stopWords.add("just");
        stopWords.add("about");
        stopWords.add("up");
        stopWords.add("out");
        stopWords.add("if");
        stopWords.add("then");
        stopWords.add("now");
        stopWords.add("here");
        stopWords.add("there");
        stopWords.add("when");
        stopWords.add("where");
        stopWords.add("why");
        stopWords.add("how");
        stopWords.add("which");
        stopWords.add("who");
        stopWords.add("whom");
        stopWords.add("what");
        stopWords.add("this");
        stopWords.add("that");
        stopWords.add("these");
        stopWords.add("those");
    }

    public void addKeyword(String keyword) {
        if (!keywords.contains(keyword) && keywords.size() < maxKeywords) {
            keywords.add(keyword);
        }
    }

    public void removeKeyword(String keyword) {
        keywords.remove(keyword);
    }

    public boolean isStopWord(String word) {
        return stopWords.contains(word.toLowerCase());
    }

    // ===== Getters & Setters =====

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getStopWords() {
        return stopWords;
    }

    public void setStopWords(List<String> stopWords) {
        this.stopWords = stopWords;
    }

    public int getMaxKeywords() {
        return maxKeywords;
    }

    public void setMaxKeywords(int maxKeywords) {
        this.maxKeywords = maxKeywords;
    }
}