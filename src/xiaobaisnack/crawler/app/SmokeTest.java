package xiaobaisnack.crawler.app;

import xiaobaisnack.crawler.core.CrawlerEngine;
import xiaobaisnack.crawler.scheduler.DepthController;
import xiaobaisnack.crawler.storage.dao.PageDao;
import xiaobaisnack.crawler.storage.entity.PageEntity;
import xiaobaisnack.crawler.url.manager.UrlNormalizer;
import xiaobaisnack.crawler.url.manager.UrlRepository;
import xiaobaisnack.crawler.utils.TimeUtils;

import java.io.File;
import java.util.List;

/**
 * 冒烟测试：不依赖网络，验证 URL 规范化、队列去重与数据库增删改查。
 */
public class SmokeTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        File tempDb = new File(System.getProperty("java.io.tmpdir"),
                "starmirage-smoke-" + System.nanoTime() + ".db");
        System.setProperty("crawler.db", tempDb.getAbsolutePath());
        System.out.println("[SmokeTest] 临时数据库: " + tempDb.getAbsolutePath());

        testUrlNormalizer();
        testQueue();
        testDatabase();

        if (args.length > 0 && args[0].equals("--net")) {
            testCrawlNetwork();
        }

        System.out.println();
        System.out.println("通过: " + passed + "，失败: " + failed);

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void testUrlNormalizer() {
        String url = UrlNormalizer.normalize("HTTP://Example.COM:80/news/?q=1#top");
        check("规范化主机/端口/斜杠/fragment", "http://example.com/news?q=1".equals(url), url);

        String httpsUrl = UrlNormalizer.normalize("https://example.com:443");
        check("https 默认端口与根路径", "https://example.com".equals(httpsUrl), httpsUrl);

        check("保留查询串", "https://a.com/p?id=2".equals(UrlNormalizer.normalize("https://a.com/p?id=2")), null);
        check("拒绝非 http(s)", UrlNormalizer.normalize("ftp://a.com/x") == null, null);
        check("拒绝空值", UrlNormalizer.normalize("   ") == null, null);
    }

    private static void testQueue() {
        UrlRepository repo = new UrlRepository();

        boolean first = repo.addUrl("https://example.com/a#x", 5);
        boolean duplicate = repo.addUrl("https://example.com/a", 5);
        repo.addUrl("https://example.com/b", 1);

        check("首次入队成功", first, null);
        check("同 URL 去重", !duplicate, null);
        check("待抓取计数", repo.size() == 2, String.valueOf(repo.size()));
        check("已发现计数", repo.getDiscoveredCount() == 2, String.valueOf(repo.getDiscoveredCount()));

        String firstPoll = repo.poll();
        check("优先级小的先出队", "https://example.com/b".equals(firstPoll), firstPoll);
        repo.markDone(firstPoll);
        check("状态更新为 DONE", repo.getState(firstPoll) != null, String.valueOf(repo.getState(firstPoll)));

        String secondPoll = repo.poll();
        check("fragment 已被剥离", "https://example.com/a".equals(secondPoll), secondPoll);
        check("队列已空", repo.isEmpty(), null);
    }

    private static void testDatabase() {
        PageDao dao = new PageDao();
        String url = "https://example.com/smoke";

        PageEntity page = new PageEntity(url, "星幻爬虫冒烟测试", "正文内容 smoke-test-keyword",
                "DONE", TimeUtils.now());
        page.setSource(PageEntity.SOURCE_LOCAL);
        dao.save(page);

        PageEntity saved = dao.getByUrl(url);
        check("保存后可查询", saved != null && saved.getId() > 0, null);
        check("抓取时间已写入", saved != null && saved.getCrawlTime() != null && !saved.getCrawlTime().isEmpty(), null);

        List<PageEntity> results = dao.search("smoke-test-keyword");
        check("搜索结果保留 id", !results.isEmpty() && results.get(0).getId() > 0, null);
        check("搜索结果保留抓取时间", !results.isEmpty() && results.get(0).getCrawlTime() != null, null);

        int originalId = saved.getId();
        PageEntity refreshed = new PageEntity(url, "二次抓取标题", "更新后的正文 smoke-test-keyword",
                "DONE", TimeUtils.now());
        dao.save(refreshed);
        PageEntity afterRefresh = dao.getByUrl(url);
        check("重复抓取不新增记录", dao.count() == 1, String.valueOf(dao.count()));
        check("重复抓取刷新标题", afterRefresh != null && "二次抓取标题".equals(afterRefresh.getTitle()), null);
        check("重复抓取保持 id 不变", afterRefresh != null && afterRefresh.getId() == originalId, null);
        saved = afterRefresh;

        saved.setTitle("已修改标题");
        int updated = dao.update(saved);
        check("更新返回受影响行数", updated > 0, String.valueOf(updated));
        check("标题已更新", "已修改标题".equals(dao.getById(saved.getId()).getTitle()), null);

        int deleted = dao.delete(saved.getId());
        check("删除返回受影响行数", deleted > 0, String.valueOf(deleted));
        check("删除后查不到", dao.getByUrl(url) == null, null);
    }

    private static void testCrawlNetwork() {
        System.out.println();
        System.out.println("[SmokeTest] 联网测试: 抓取 https://example.com");

        UrlRepository repo = new UrlRepository();
        CrawlerEngine engine = new CrawlerEngine(repo, new DepthController(0), 5);
        repo.addUrl("https://example.com", 1);

        boolean handled = engine.crawlNext();
        check("引擎处理了任务", handled, null);

        PageDao dao = new PageDao();
        PageEntity page = dao.getByUrl("https://example.com");
        check("页面已入库", page != null, null);
        check("标题已解析", page != null && page.getTitle() != null && !page.getTitle().isEmpty(),
                page == null ? null : page.getTitle());
        check("正文非空", page != null && page.getContent() != null && !page.getContent().isEmpty(), null);
        check("抓取时间已记录", page != null && page.getCrawlTime() != null && !page.getCrawlTime().isEmpty(), null);
    }

    private static void check(String name, boolean condition, String detail) {
        if (condition) {
            passed++;
            System.out.println("  [PASS] " + name);
        } else {
            failed++;
            System.out.println("  [FAIL] " + name + (detail == null ? "" : " -> " + detail));
        }
    }
}