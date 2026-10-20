package xiaobaisnack.crawler.search.engine;

import java.util.*;
import xiaobaisnack.crawler.config.SiteConfig;
import xiaobaisnack.crawler.search.rank.ResultRanker;
import xiaobaisnack.crawler.storage.dao.PageDao;
import xiaobaisnack.crawler.storage.entity.PageEntity;

/**
 * ⭐ 搜索引擎主类（混合搜索版）
 *
 * 搜索流程：
 * 1. 先查本地数据库
 * 2. 同时借助外部搜索引擎搜索
 * 3. 合并结果 + 按关键词匹配加权排序
 * 4. 返回排序后的结果
 */
public class SearchEngine {

    private final PageDao pageDao;
    private final ExternalSearcher externalSearcher;
    private final ResultRanker ranker;

    public SearchEngine(SiteConfig siteConfig) {
        this.pageDao = new PageDao();
        this.externalSearcher = new ExternalSearcher(siteConfig);
        this.ranker = new ResultRanker();
    }

    /**
     * 混合搜索
     * @param keyword 搜索关键词
     * @return 排序后的搜索结果
     */
    public List<PageEntity> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String kw = keyword.trim();
        Set<String> seenUrls = new HashSet<>();
        List<PageEntity> allResults = new ArrayList<>();

        // 1. 本地数据库搜索
        System.out.println("[SearchEngine] 本地搜索: " + kw);
        List<PageEntity> localResults = pageDao.search(kw);
        for (PageEntity page : localResults) {
            if (page.getSource() == null) {
                page.setSource(PageEntity.SOURCE_LOCAL);
            }
            if (seenUrls.add(page.getUrl())) {
                allResults.add(page);
            }
        }
        System.out.println("[SearchEngine] 本地找到 " + localResults.size() + " 条结果");

        // 2. 外部搜索引擎搜索
        System.out.println("[SearchEngine] 外部搜索: " + kw);
        List<PageEntity> externalResults = externalSearcher.search(kw);
        for (PageEntity page : externalResults) {
            if (seenUrls.add(page.getUrl())) {
                allResults.add(page);
            }
        }
        System.out.println("[SearchEngine] 外部找到 " + externalResults.size() + " 条结果（去重后新增）");

        // 3. 按关键词匹配加权排序
        System.out.println("[SearchEngine] 排序中... 共 " + allResults.size() + " 条结果");
        List<PageEntity> ranked = ranker.rank(allResults, kw);

        return ranked;
    }

    /**
     * 仅本地搜索（不触发外部搜索）
     */
    public List<PageEntity> searchLocal(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<PageEntity> results = pageDao.search(keyword.trim());
        return ranker.rank(results, keyword.trim());
    }

    /**
     * 获取所有页面
     */
    public List<PageEntity> getAllPages() {
        return pageDao.getAllPages();
    }

    /**
     * 获取页面总数
     */
    public int getPageCount() {
        return pageDao.count();
    }
}