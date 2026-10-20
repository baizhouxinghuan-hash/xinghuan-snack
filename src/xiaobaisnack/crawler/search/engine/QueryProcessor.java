package xiaobaisnack.crawler.search.engine;

import java.util.*;

/**
 * 查询处理器
 *
 * 功能：
 * 将用户输入的搜索语句进行处理：
 * 1. 分词
 * 2. 标准化（小写）
 * 3. 去除无效词
 */
public class QueryProcessor {

    /**
     * 处理查询字符串
     *
     * @param query 用户输入（例如："Java 爬虫 教程"）
     * @return 处理后的关键词列表
     */
    public List<String> process(String query) {

        // 转小写 + 分词
        String[] words = query.toLowerCase().split("\\W+");

        List<String> result = new ArrayList<>();

        for (String word : words) {

            // 过滤空字符串
            if (!word.isEmpty()) {
                result.add(word);
            }
        }

        return result;
    }
}