package xiaobaisnack.crawler.api;

import xiaobaisnack.crawler.core.CrawlerEngine;
import xiaobaisnack.crawler.scheduler.TaskScheduler;
import xiaobaisnack.crawler.storage.dao.PageDao;
import xiaobaisnack.crawler.storage.entity.PageEntity;
import xiaobaisnack.crawler.url.manager.UrlRepository;

import java.util.List;

/**
 * 爬虫控制器（对外接口）。
 *
 * <p>负责启动/停止爬虫、添加任务、查询状态。所有任务统一进入
 * UrlRepository 这一个队列，避免多队列导致的统计失真。</p>
 */
public class CrawlController {

    private final UrlRepository urlRepo;
    private final CrawlerEngine engine;
    private final TaskScheduler scheduler;
    private final PageDao pageDao;
    private volatile boolean running = false;

    public CrawlController(UrlRepository urlRepo, CrawlerEngine engine, TaskScheduler scheduler) {
        this.urlRepo = urlRepo;
        this.engine = engine;
        this.scheduler = scheduler;
        this.pageDao = new PageDao();
    }

    /**
     * 启动爬虫。
     *
     * @param seedUrl 种子 URL
     */
    public void startCrawl(String seedUrl) {
        if (running) {
            System.out.println("[CrawlController] 爬虫已在运行中");
            return;
        }
        if (seedUrl == null || seedUrl.trim().isEmpty()) {
            System.out.println("[CrawlController] 种子 URL 为空，已忽略");
            return;
        }

        System.out.println("[CrawlController] 启动爬虫，种子URL: " + seedUrl);

        if (!urlRepo.addUrl(seedUrl, 1)) {
            System.out.println("[CrawlController] 种子 URL 无效或已在队列中: " + seedUrl);
        }

        scheduler.start();
        running = true;
        System.out.println("[CrawlController] 爬虫已启动");
    }

    /**
     * 停止爬虫。
     */
    public void stopCrawl() {
        if (!running) {
            System.out.println("[CrawlController] 爬虫未在运行");
            return;
        }

        scheduler.stop();
        running = false;
        System.out.println("[CrawlController] 爬虫已停止");
    }

    /**
     * 添加 URL 到爬取队列。
     */
    public void addUrl(String url) {
        addUrl(url, 5);
    }

    /**
     * 添加 URL 到爬取队列（带优先级，数字越小越优先）。
     *
     * @return 是否真正入队
     */
    public boolean addUrl(String url, int priority) {
        boolean added = urlRepo.addUrl(url, priority);
        System.out.println("[CrawlController] " + (added ? "添加URL: " : "跳过（已存在或无效）: ")
                + url + " 优先级: " + priority);
        return added;
    }

    /**
     * 获取爬虫状态。
     */
    public CrawlStatus getStatus() {
        CrawlStatus status = new CrawlStatus();
        status.running = this.running;
        status.queueSize = urlRepo.size();
        status.totalPages = pageDao.count();
        status.activeWorkers = engine.getActiveWorkers();
        return status;
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * 获取所有已抓取页面。
     */
    public List<PageEntity> getAllPages() {
        return pageDao.getAllPages();
    }

    /**
     * 搜索页面（仅本地数据库）。
     */
    public List<PageEntity> searchPages(String keyword) {
        return pageDao.search(keyword);
    }

    /**
     * 爬虫状态快照。
     */
    public static class CrawlStatus {
        public boolean running;
        public int queueSize;
        public int totalPages;
        public int activeWorkers;

        @Override
        public String toString() {
            return "CrawlStatus{running=" + running +
                    ", queueSize=" + queueSize +
                    ", activeWorkers=" + activeWorkers +
                    ", totalPages=" + totalPages + "}";
        }
    }
}