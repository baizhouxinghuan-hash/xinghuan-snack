package xiaobaisnack.crawler.search.index;

import xiaobaisnack.crawler.storage.dao.PageDao;
import xiaobaisnack.crawler.storage.entity.PageEntity;

import java.util.List;

/**
 * 索引构建器
 *
 * 功能：
 * 从数据库加载页面数据，并构建倒排索引
 */
public class IndexBuilder {

    // 倒排索引实例
    private InvertedIndex index = new InvertedIndex();

    // 数据访问对象（从SQLite读取页面）
    private PageDao pageDao = new PageDao();

    /**
     * 构建索引
     *
     * @return 构建好的倒排索引
     */
    public InvertedIndex build() {

        // 从数据库获取所有页面
        List<PageEntity> pages = pageDao.getAllPages();

        // 遍历每个页面，加入索引
        for (PageEntity page : pages) {

            // docId + 内容
            String fullText = page.getTitle() + " " + page.getContent() + " " + page.getUrl();

            index.addDocument(page.getId(), fullText);
        }

        return index;
    }
}