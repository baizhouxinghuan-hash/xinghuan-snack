package xiaobaisnack.crawler.scheduler;

import xiaobaisnack.crawler.core.CrawlerEngine;
import xiaobaisnack.crawler.url.manager.UrlRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 爬虫任务调度器。
 *
 * <p>工作线程循环调用 CrawlerEngine.crawlNext()，只有队列为空时才休眠，
 * 避免固定间隔拖慢整体抓取速度。</p>
 */
public class TaskScheduler {

    private final UrlRepository urlRepo;
    private final CrawlerEngine engine;
    private final int maxThreads;
    private final int idleInterval;
    private ExecutorService executor;
    private volatile boolean running = false;

    public TaskScheduler(UrlRepository urlRepo, CrawlerEngine engine, int maxThreads, int idleInterval) {
        this.urlRepo = urlRepo;
        this.engine = engine;
        this.maxThreads = Math.max(1, maxThreads);
        this.idleInterval = Math.max(200, idleInterval);
    }

    public void start() {
        if (running) {
            return;
        }

        running = true;
        executor = Executors.newFixedThreadPool(maxThreads, new ThreadFactory() {
            private final AtomicInteger index = new AtomicInteger();

            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, "starmirage-worker-" + index.incrementAndGet());
                thread.setDaemon(true);
                return thread;
            }
        });

        System.out.println("[TaskScheduler] 启动，线程数: " + maxThreads);

        for (int i = 0; i < maxThreads; i++) {
            executor.submit(() -> {
                boolean idleLogged = false;
                while (running && !Thread.currentThread().isInterrupted()) {
                    try {
                        if (engine.crawlNext()) {
                            idleLogged = false;
                            continue;
                        }
                        if (!idleLogged) {
                            System.out.println("[TaskScheduler] 队列为空，等待新任务...");
                            idleLogged = true;
                        }
                        Thread.sleep(idleInterval);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        System.err.println("[TaskScheduler] 抓取出错: " + e.getMessage());
                    }
                }
            });
        }
    }

    public void stop() {
        running = false;
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
        System.out.println("[TaskScheduler] 已停止");
    }

    public boolean isRunning() {
        return running;
    }
}