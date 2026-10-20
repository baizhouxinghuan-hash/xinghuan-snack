package xiaobaisnack.crawler.core;

import xiaobaisnack.crawler.url.manager.UrlRepository;

public class CrawlWorker implements Runnable {

    private final UrlRepository repo;
    private final PageFetcher fetcher;

    public CrawlWorker(UrlRepository repo) {
        this.repo = repo;
        this.fetcher = new PageFetcher();
    }

    @Override
    public void run() {
        while (true) {
            String url = repo.poll();
            if (url == null) {
                // 队列为空，线程退出
                break;
            }

            System.out.println(Thread.currentThread().getName() + " 抓取: " + url);

            String html = fetcher.fetch(url);
            if (html != null) {
                repo.markDone(url);
                System.out.println(Thread.currentThread().getName() + " 完成: " + url);
                // 🔹 这里后续可以加入 PageParser / ContentCleaner
            } else {
                repo.markFailed(url);
                System.out.println(Thread.currentThread().getName() + " 失败: " + url);
            }
        }
    }
}