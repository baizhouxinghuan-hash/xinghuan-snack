package xiaobaisnack.crawler.ui.model;

/**
 * ⭐ 统计视图模型
 *
 * 功能：
 * - 封装爬虫统计数据
 * - 用于UI统计面板显示
 */
public class StatsViewModel {

    private int totalUrls;
    private int crawledUrls;
    private int failedUrls;
    private int pendingUrls;
    private long startTime;
    private long elapsedTime;

    public StatsViewModel() {
        this.totalUrls = 0;
        this.crawledUrls = 0;
        this.failedUrls = 0;
        this.pendingUrls = 0;
        this.startTime = System.currentTimeMillis();
        this.elapsedTime = 0;
    }

    /**
     * 更新统计数据
     */
    public void update(int totalUrls, int crawledUrls, int failedUrls, int pendingUrls) {
        this.totalUrls = totalUrls;
        this.crawledUrls = crawledUrls;
        this.failedUrls = failedUrls;
        this.pendingUrls = pendingUrls;
        this.elapsedTime = System.currentTimeMillis() - startTime;
    }

    /**
     * 格式化耗时
     */
    public String getFormattedElapsed() {
        long seconds = elapsedTime / 1000;
        if (seconds < 60) return seconds + "秒";
        long minutes = seconds / 60;
        seconds = seconds % 60;
        if (minutes < 60) return minutes + "分" + seconds + "秒";
        long hours = minutes / 60;
        minutes = minutes % 60;
        return hours + "小时" + minutes + "分" + seconds + "秒";
    }

    /**
     * 获取进度百分比
     */
    public int getProgress() {
        if (totalUrls == 0) return 0;
        return (int) ((crawledUrls * 100.0) / totalUrls);
    }

    // ===== Getters =====

    public int getTotalUrls() { return totalUrls; }
    public int getCrawledUrls() { return crawledUrls; }
    public int getFailedUrls() { return failedUrls; }
    public int getPendingUrls() { return pendingUrls; }
    public long getElapsedTime() { return elapsedTime; }
}