package xiaobaisnack.crawler.scheduler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * ⭐ 定时任务调度器
 *
 * 功能：
 * - 定时触发 URL 抓取任务
 * - 配合 TaskScheduler 和 CrawlerEngine 使用
 */
public class TimeScheduler {

    private final Timer timer = new Timer(true);

    /**
     * 注册周期任务
     *
     * @param intervalMs 间隔时间（毫秒）
     * @param task       需要执行的任务
     */
    public void schedule(long intervalMs, Runnable task) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        }, 0, intervalMs);
    }

    /**
     * 停止定时任务
     */
    public void stop() {
        timer.cancel();
    }
}