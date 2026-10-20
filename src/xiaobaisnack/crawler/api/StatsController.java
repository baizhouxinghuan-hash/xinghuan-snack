package xiaobaisnack.crawler.api;

import xiaobaisnack.crawler.storage.dao.PageDao;
import xiaobaisnack.crawler.storage.dao.UrlDao;
import xiaobaisnack.crawler.storage.entity.PageEntity;
import xiaobaisnack.crawler.storage.entity.UrlEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ⭐ 统计控制器
 *
 * 功能：
 * - 提供爬虫统计数据
 * - 页面统计
 * - URL统计
 */
public class StatsController {

    private final PageDao pageDao;
    private final UrlDao urlDao;

    public StatsController() {
        this.pageDao = new PageDao();
        this.urlDao = new UrlDao();
    }

    /**
     * 获取综合统计信息
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // URL统计
        stats.put("totalUrls", urlDao.count());
        stats.put("newUrls", urlDao.countByState("NEW"));
        stats.put("doneUrls", urlDao.countByState("DONE"));
        stats.put("failedUrls", urlDao.countByState("FAILED"));

        // 页面统计
        stats.put("totalPages", pageDao.count());

        return stats;
    }

    /**
     * 获取URL统计
     */
    public UrlStats getUrlStats() {
        UrlStats stats = new UrlStats();
        stats.total = urlDao.count();
        stats.newCount = urlDao.countByState("NEW");
        stats.queued = urlDao.countByState("QUEUED");
        stats.fetching = urlDao.countByState("FETCHING");
        stats.done = urlDao.countByState("DONE");
        stats.failed = urlDao.countByState("FAILED");
        return stats;
    }

    /**
     * 获取页面统计
     */
    public PageStats getPageStats() {
        PageStats stats = new PageStats();
        stats.total = pageDao.count();
        return stats;
    }

    /**
     * 获取所有URL
     */
    public List<UrlEntity> getAllUrls() {
        return urlDao.getAll();
    }

    /**
     * 获取所有页面
     */
    public List<PageEntity> getAllPages() {
        return pageDao.getAllPages();
    }

    /**
     * URL统计
     */
    public static class UrlStats {
        public int total;
        public int newCount;
        public int queued;
        public int fetching;
        public int done;
        public int failed;

        @Override
        public String toString() {
            return "UrlStats{total=" + total + ", new=" + newCount +
                    ", queued=" + queued + ", fetching=" + fetching +
                    ", done=" + done + ", failed=" + failed + "}";
        }
    }

    /**
     * 页面统计
     */
    public static class PageStats {
        public int total;

        @Override
        public String toString() {
            return "PageStats{total=" + total + "}";
        }
    }
}