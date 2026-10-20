package xiaobaisnack.crawler.scheduler;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ⭐ 爬取深度控制器
 *
 * 功能：
 * - 记录每个 URL 的深度
 * - 防止无限递归抓取
 */
public class DepthController {

    private final int maxDepth;   // 最大抓取深度
    private final ConcurrentHashMap<String, Integer> urlDepthMap = new ConcurrentHashMap<>();

    public DepthController(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    /**
     * 设置初始深度
     */
    public void setDepth(String url, int depth) {
        urlDepthMap.put(url, depth);
    }

    /**
     * 检查 URL 是否可以抓取子链接
     */
    public boolean canExpand(String url) {
        return urlDepthMap.getOrDefault(url, 0) < maxDepth;
    }

    /**
     * 记录子链接深度
     */
    public void recordChild(String parentUrl, String childUrl) {
        int parentDepth = urlDepthMap.getOrDefault(parentUrl, 0);
        urlDepthMap.put(childUrl, parentDepth + 1);
    }

    /**
     * 获取 URL 当前深度
     */
    public int getDepth(String url) {
        return urlDepthMap.getOrDefault(url, 0);
    }
}