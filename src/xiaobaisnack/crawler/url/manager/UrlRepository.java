package xiaobaisnack.crawler.url.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * URL 待抓取队列（唯一数据源）。
 *
 * <p>同时承担规范化、去重、优先级排序、状态记录与待抓取计数。
 * 之前 PriorityManager 与 UrlRepository 各存一份队列，导致统计不准、
 * 工作线程无法退出的问题，已在此合并解决。</p>
 */
public class UrlRepository {

    private static final int DEFAULT_PRIORITY = 5;

    private final PriorityBlockingQueue<QueueItem> queue = new PriorityBlockingQueue<>();
    private final Map<String, UrlStateMachine.State> stateMap = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();
    private final AtomicLong discovered = new AtomicLong();

    public boolean addUrl(String url) {
        return addUrl(url, DEFAULT_PRIORITY);
    }

    /**
     * 添加入队任务。
     *
     * @param priority 数字越小越优先（种子 URL 建议使用 1）
     * @return 是否真正入队（已存在则返回 false）
     */
    public boolean addUrl(String url, int priority) {
        String normalized = UrlNormalizer.normalize(url);
        if (normalized == null) {
            return false;
        }

        if (stateMap.putIfAbsent(normalized, UrlStateMachine.State.QUEUED) != null) {
            return false;
        }

        discovered.incrementAndGet();
        queue.offer(new QueueItem(normalized, priority, sequence.incrementAndGet()));
        return true;
    }

    /**
     * 取出优先级最高的 URL；队列为空时返回 null。
     */
    public String poll() {
        QueueItem item = queue.poll();
        if (item == null) {
            return null;
        }
        stateMap.put(item.url, UrlStateMachine.State.FETCHING);
        return item.url;
    }

    public void markDone(String url) {
        updateState(url, UrlStateMachine.State.DONE);
    }

    public void markFailed(String url) {
        updateState(url, UrlStateMachine.State.FAILED);
    }

    private void updateState(String url, UrlStateMachine.State state) {
        String normalized = UrlNormalizer.normalize(url);
        if (normalized != null) {
            stateMap.put(normalized, state);
        }
    }

    /**
     * 待抓取数量（不是历史发现数量）。
     */
    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * 已发现的 URL 总数，用于 maxUrls 上限判断。
     */
    public int getDiscoveredCount() {
        return (int) discovered.get();
    }

    public boolean isKnown(String url) {
        String normalized = UrlNormalizer.normalize(url);
        return normalized != null && stateMap.containsKey(normalized);
    }

    public UrlStateMachine.State getState(String url) {
        String normalized = UrlNormalizer.normalize(url);
        return normalized == null ? null : stateMap.get(normalized);
    }

    public void clear() {
        queue.clear();
        stateMap.clear();
        discovered.set(0);
    }

    private static final class QueueItem implements Comparable<QueueItem> {
        private final String url;
        private final int priority;
        private final long sequence;

        private QueueItem(String url, int priority, long sequence) {
            this.url = url;
            this.priority = priority;
            this.sequence = sequence;
        }

        @Override
        public int compareTo(QueueItem other) {
            int byPriority = Integer.compare(priority, other.priority);
            return byPriority != 0 ? byPriority : Long.compare(sequence, other.sequence);
        }
    }
}