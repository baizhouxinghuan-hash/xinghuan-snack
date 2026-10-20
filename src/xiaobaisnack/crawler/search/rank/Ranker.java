package xiaobaisnack.crawler.search.rank;

import xiaobaisnack.crawler.storage.dao.PageDao;
import xiaobaisnack.crawler.storage.entity.PageEntity;

import java.util.*;

public class Ranker {

    private PageDao pageDao = new PageDao(); // ⭐建议放成员变量

    public List<Integer> rank(List<Map<Integer, Integer>> results,
                              List<String> keywords,
                              int totalDocs) {

        Map<Integer, Double> scoreMap = new HashMap<>();

        for (int i = 0; i < results.size(); i++) {

            Map<Integer, Integer> map = results.get(i);
            String word = keywords.get(i);

            int docFreq = map.size();
            double idf = Math.log((double) (totalDocs + 1) / (docFreq + 1));

            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {

                int docId = entry.getKey();
                int tf = entry.getValue();

                PageEntity page = pageDao.getById(docId);

                double score = 0;

                // 正文 ×1
                score += tf * idf;

                // 标题 ×5
                if (page.getTitle() != null &&
                    page.getTitle().toLowerCase().contains(word)) {
                    score += 5 * idf;
                }

                // URL ×2
                if (page.getUrl() != null &&
                    page.getUrl().toLowerCase().contains(word)) {
                    score += 2 * idf;
                }

                scoreMap.put(docId,
                        scoreMap.getOrDefault(docId, 0.0) + score);
            }
        }

        List<Integer> sorted = new ArrayList<>(scoreMap.keySet());

        sorted.sort((a, b) -> Double.compare(scoreMap.get(b), scoreMap.get(a)));

        return sorted;
    }
}