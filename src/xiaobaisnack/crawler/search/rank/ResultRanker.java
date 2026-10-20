package xiaobaisnack.crawler.search.rank;

import xiaobaisnack.crawler.storage.entity.PageEntity;

import java.util.*;

/**
 * ⭐ 搜索结果排序器
 *
 * 功能：
 * - 按URL/标题/内容中关键词匹配进行加权排序
 * - URL匹配关键词：权重 1
 * - 标题匹配关键词：权重 3
 * - 内容匹配关键词：权重 2
 */
public class ResultRanker {

    /**
     * 对搜索结果按关键词匹配加权排序
     * @param pages 搜索结果列表
     * @param keyword 搜索关键词
     * @return 排序后的结果列表
     */
    public List<PageEntity> rank(List<PageEntity> pages, String keyword) {
        if (pages == null || pages.isEmpty()) {
            return new ArrayList<>();
        }

        String lowerKeyword = keyword.toLowerCase();

        // 计算每条结果的匹配分数
        List<ScoredPage> scoredPages = new ArrayList<>();
        for (PageEntity page : pages) {
            int score = calculateScore(page, lowerKeyword);
            scoredPages.add(new ScoredPage(page, score));
        }

        // 按分数降序排序
        scoredPages.sort((a, b) -> Integer.compare(b.score, a.score));

        // 返回排序后的结果
        List<PageEntity> ranked = new ArrayList<>();
        for (ScoredPage sp : scoredPages) {
            ranked.add(sp.page);
        }

        return ranked;
    }

    /**
     * 计算单条结果的匹配分数
     */
    private int calculateScore(PageEntity page, String lowerKeyword) {
        int score = 0;

        // URL匹配：权重1
        if (page.getUrl() != null) {
            String url = page.getUrl().toLowerCase();
            score += countMatches(url, lowerKeyword) * 1;
        }

        // 标题匹配：权重3
        if (page.getTitle() != null) {
            String title = page.getTitle().toLowerCase();
            score += countMatches(title, lowerKeyword) * 3;
        }

        // 内容匹配：权重2
        if (page.getContent() != null) {
            String content = page.getContent().toLowerCase();
            score += countMatches(content, lowerKeyword) * 2;
        }

        return score;
    }

    /**
     * 计算关键词在文本中出现的次数
     */
    private int countMatches(String text, String keyword) {
        if (text == null || keyword == null || keyword.isEmpty()) {
            return 0;
        }
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(keyword, index)) != -1) {
            count++;
            index += keyword.length();
        }
        return count;
    }

    /**
     * 内部类：带分数的页面
     */
    private static class ScoredPage {
        PageEntity page;
        int score;

        ScoredPage(PageEntity page, int score) {
            this.page = page;
            this.score = score;
        }
    }
}