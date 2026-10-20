package xiaobaisnack.crawler.core;

import xiaobaisnack.crawler.cleaner.ContentCleaner;
import xiaobaisnack.crawler.scheduler.DepthController;
import xiaobaisnack.crawler.storage.dao.PageDao;
import xiaobaisnack.crawler.storage.entity.PageEntity;
import xiaobaisnack.crawler.url.filter.UrlFilter;
import xiaobaisnack.crawler.url.manager.UrlNormalizer;
import xiaobaisnack.crawler.url.manager.UrlRepository;
import xiaobaisnack.crawler.utils.TimeUtils;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 爬虫引擎。
 *
 * <p>流程：UrlRepository 取任务 → 下载 → 清洗正文 → 解析标题 → 入库 → 扩展子链接。</p>
 */
public class CrawlerEngine {

    private final UrlRepository repo;
    private final PageFetcher fetcher;
    private final PageDao pageDao;
    private final DepthController depthController;
    private final UrlFilter urlFilter = new UrlFilter();
    private final int maxUrls;
    private final AtomicInteger activeWorkers = new AtomicInteger();

    public CrawlerEngine(UrlRepository repo, DepthController depthController, int maxUrls) {
        this.repo = repo;
        this.fetcher = new PageFetcher();
        this.pageDao = new PageDao();
        this.depthController = depthController;
        this.maxUrls = maxUrls;
    }

    /**
     * 处理一个队列任务。
     *
     * @return true 表示本次取到了 URL（无论成功失败），false 表示队列为空
     */
    public boolean crawlNext() {
        String url = repo.poll();
        if (url == null) {
            return false;
        }

        activeWorkers.incrementAndGet();
        try {
            System.out.println("[Crawler] 开始抓取: " + url);

            String html = fetcher.fetch(url);
            if (html == null) {
                repo.markFailed(url);
                return true;
            }

            String content = new ContentCleaner().clean(html);
            PageParser.PageData data = new PageParser().parse(html);

            PageEntity page = new PageEntity(url, data.title, content, "DONE", TimeUtils.now());
            pageDao.save(page);
            repo.markDone(url);

            System.out.println("[Crawler] 完成: " + url);
            System.out.println("  标题: " + data.title);
            System.out.println("  正文长度: " + (content == null ? 0 : content.length()));

            if (depthController.canExpand(url)) {
                Set<String> links = LinkExtractor.extractLinks(html, url);
                for (String link : links) {
                    if (repo.getDiscoveredCount() >= maxUrls) {
                        break;
                    }
                    if (!urlFilter.allow(link)) {
                        continue;
                    }
                    String normalized = UrlNormalizer.normalize(link);
                    if (normalized == null) {
                        continue;
                    }
                    depthController.recordChild(url, normalized);
                    repo.addUrl(normalized, 5);
                }
            }
            return true;

        } catch (Exception e) {
            repo.markFailed(url);
            System.err.println("[Crawler] 抓取失败: " + url + " - " + e.getMessage());
            return true;
        } finally {
            activeWorkers.decrementAndGet();
        }
    }

    /**
     * 是否有工作线程正在抓取。
     */
    public boolean isCrawling() {
        return activeWorkers.get() > 0;
    }

    public int getActiveWorkers() {
        return activeWorkers.get();
    }

    /**
     * 独立启动引擎（不经过 TaskScheduler 时使用）。
     */
    public void start(int threads) {
        for (int i = 0; i < Math.max(1, threads); i++) {
            Thread thread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    if (!crawlNext()) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }, "starmirage-engine-" + (i + 1));
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * 单独获取页面 HTML（不经过队列）。
     */
    public String fetchPage(String url) {
        String html = fetcher.fetch(url);
        return html == null ? "" : html;
    }
}