package xiaobaisnack.crawler.search.index;

import java.util.*;

/**
 * 倒排索引核心类
 *
 * 功能：
 * 关键词 -> 出现该关键词的文档ID列表
 *
 * 示例：
 * "java" -> [1, 3, 5]
 */
public class InvertedIndex {

    /**
     * 倒排索引存储结构
     * key: 关键词
     * value: 包含该关键词的文档ID列表
     */
    private Map<String, Map<Integer, Integer>> index = new HashMap<>();

    /**
     * 添加一个文档到索引中
     *
     * @param docId 文档ID（来自数据库）
     * @param content 文档内容（正文）
     */
    public void addDocument(int docId, String content) {

        // 简单分词（按非字母数字分割）
        String[] words = content.toLowerCase().split("\\W+");

        for (String word : words) {

            // 跳过空词
            if (word.isEmpty()) continue;

            // 如果该词不存在，则初始化列表
            index.putIfAbsent(word, new HashMap<>());

            // 取出该词对应的 docMap
            Map<Integer, Integer> docMap = index.get(word);

            //  词频+1
           docMap.put(docId, docMap.getOrDefault(docId, 0) + 1);
        }
    }

    /**
     * 根据关键词查询文档
     *
     * @param keyword 搜索关键词
     * @return 包含该关键词的文档ID列表
     */
    public Map<Integer, Integer> search(String keyword) {
        return index.getOrDefault(keyword.toLowerCase(), new HashMap<>());
    }
}