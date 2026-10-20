package xiaobaisnack.crawler.app;

import xiaobaisnack.crawler.config.CrawlConfig;
import xiaobaisnack.crawler.config.SiteConfig;
import xiaobaisnack.crawler.core.CrawlerEngine;
import xiaobaisnack.crawler.scheduler.DepthController;
import xiaobaisnack.crawler.scheduler.TaskScheduler;
import xiaobaisnack.crawler.search.engine.SearchEngine;
import xiaobaisnack.crawler.storage.dao.PageDao;
import xiaobaisnack.crawler.storage.entity.PageEntity;
import xiaobaisnack.crawler.ui.MainWindow;
import xiaobaisnack.crawler.url.manager.UrlRepository;

import java.util.List;
import java.util.Scanner;

/**
 * 星幻爬虫 / StarMirage Crawler 主入口。
 *
 * <p>职责：初始化配置与模块，然后进入命令行或图形界面模式。</p>
 */
public class MainApplication {

    private static CrawlConfig config;
    private static SiteConfig siteConfig;
    private static UrlRepository urlRepo;
    private static CrawlerEngine engine;
    private static SearchEngine searchEngine;
    private static TaskScheduler scheduler;
    private static PageDao pageDao;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  星幻爬虫 StarMirage Crawler v1.1.0");
        System.out.println("  桌面端网页采集与本地全文检索工具");
        System.out.println("========================================");
        System.out.println();

        initConfig();
        initModules();

        if (args.length > 0 && args[0].equals("--gui")) {
            runGuiMode();
        } else {
            runCliMode();
        }
    }

    private static void initConfig() {
        config = new CrawlConfig();
        config.setMaxDepth(3);
        config.setMaxThreads(4);
        config.setMaxUrls(100);
        config.setRequestDelay(1000);
        config.setUserAgent("Mozilla/5.0 (compatible; StarMirageCrawler/1.0)");

        siteConfig = new SiteConfig();
        // 外部搜索默认关闭，需要时自行配置：
        // siteConfig.setSearchUrl("https://www.bing.com/search?q={keyword}");

        System.out.println("[配置] 初始化完成");
    }

    private static void initModules() {
        urlRepo = new UrlRepository();
        DepthController depthController = new DepthController(config.getMaxDepth());
        engine = new CrawlerEngine(urlRepo, depthController, config.getMaxUrls());
        searchEngine = new SearchEngine(siteConfig);
        scheduler = new TaskScheduler(urlRepo, engine, config.getMaxThreads(), config.getRequestDelay());
        pageDao = new PageDao();

        System.out.println("[模块] 所有模块初始化完成");
    }

    private static void runCliMode() {
        System.out.println();
        System.out.println("进入命令行交互模式，输入 help 查看命令。");
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }

            String[] parts = input.split("\\s+", 2);
            String command = parts[0].toLowerCase();

            switch (command) {
                case "search":
                    if (parts.length < 2) {
                        System.out.println("用法: search <关键词>");
                        break;
                    }
                    handleSearch(parts[1]);
                    break;
                case "crawl":
                    if (parts.length < 2) {
                        System.out.println("用法: crawl <URL>");
                        break;
                    }
                    handleCrawl(parts[1]);
                    break;
                case "start":
                    if (parts.length < 2) {
                        System.out.println("用法: start <种子URL>");
                        break;
                    }
                    handleStart(parts[1]);
                    break;
                case "stats":
                    handleStats();
                    break;
                case "help":
                    printHelp();
                    break;
                case "exit":
                    scheduler.stop();
                    System.out.println("再见！");
                    scanner.close();
                    return;
                default:
                    System.out.println("未知命令，输入 help 查看帮助");
            }
        }
    }

    private static void handleSearch(String keyword) {
        System.out.println("搜索: " + keyword);
        List<PageEntity> results = searchEngine.search(keyword);

        if (results.isEmpty()) {
            System.out.println("未找到结果，建议先抓取数据。");
        } else {
            System.out.println("找到 " + results.size() + " 条结果：");
            for (int i = 0; i < results.size(); i++) {
                PageEntity page = results.get(i);
                System.out.println("  [" + (i + 1) + "] " + page.getTitle());
                System.out.println("      URL: " + page.getUrl());
                String content = page.getContent();
                if (content != null && content.length() > 100) {
                    content = content.substring(0, 100) + "...";
                }
                System.out.println("      摘要: " + content);
                System.out.println();
            }
        }
    }

    private static void handleCrawl(String url) {
        System.out.println("抓取: " + url);
        String html = engine.fetchPage(url);
        if (html != null && !html.isEmpty()) {
            System.out.println("抓取成功，内容长度: " + html.length() + " 字符");
        } else {
            System.out.println("抓取失败");
        }
    }

    private static void handleStart(String seedUrl) {
        System.out.println("启动爬虫，种子URL: " + seedUrl);

        if (!urlRepo.addUrl(seedUrl, 1)) {
            System.out.println("种子 URL 无效或已在队列中");
        }
        scheduler.start();

        System.out.println("爬虫已启动，输入 stats 查看进度");
    }

    private static void handleStats() {
        System.out.println("=== 系统统计 ===");
        System.out.println("待抓取队列: " + urlRepo.size());
        System.out.println("已发现URL: " + urlRepo.getDiscoveredCount());
        System.out.println("正在抓取: " + engine.getActiveWorkers());
        System.out.println("已入库页面: " + pageDao.count());
        System.out.println("最大深度: " + config.getMaxDepth());
        System.out.println("最大线程数: " + config.getMaxThreads());
        System.out.println("最大URL数: " + config.getMaxUrls());
    }

    private static void printHelp() {
        System.out.println("=== 星幻爬虫 StarMirage Crawler 帮助 ===");
        System.out.println("命令：");
        System.out.println("  search <关键词>  本地数据库全文搜索");
        System.out.println("  crawl <URL>      抓取单个 URL 并打印长度");
        System.out.println("  start <种子URL>  启动多线程爬虫");
        System.out.println("  stats            查看队列与入库统计");
        System.out.println("  help             显示本帮助");
        System.out.println("  exit             退出");
        System.out.println();
        System.out.println("启动参数：");
        System.out.println("  --gui   图形界面（默认命令行）");
        System.out.println("  -Dcrawler.db=<路径>  指定数据库文件");
    }

    private static void runGuiMode() {
        System.out.println("启动图形界面...");
        MainWindow.launchApp(searchEngine, engine, urlRepo);
    }
}