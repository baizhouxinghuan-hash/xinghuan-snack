package xiaobaisnack.crawler.search.index;

import xiaobaisnack.crawler.storage.entity.PageEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ⭐ 文档存储
 *
 * 功能：
 * - 内存中缓存页面文档
 * - 快速通过docId获取文档
 * - 线程安全
 */
public class DocumentStore {

    private final Map<Integer, PageEntity> documents = new ConcurrentHashMap<>();

    /**
     * 添加文档
     */
    public void addDocument(int docId, PageEntity page) {
        documents.put(docId, page);
    }

    /**
     * 获取文档
     */
    public PageEntity getDocument(int docId) {
        return documents.get(docId);
    }

    /**
     * 移除文档
     */
    public void removeDocument(int docId) {
        documents.remove(docId);
    }

    /**
     * 获取文档数量
     */
    public int size() {
        return documents.size();
    }

    /**
     * 清空所有文档
     */
    public void clear() {
        documents.clear();
    }

    /**
     * 检查文档是否存在
     */
    public boolean contains(int docId) {
        return documents.containsKey(docId);
    }

    /**
     * 获取所有文档
     */
    public Map<Integer, PageEntity> getAllDocuments() {
        return new ConcurrentHashMap<>(documents);
    }
}