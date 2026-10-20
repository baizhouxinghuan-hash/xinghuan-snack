package xiaobaisnack.crawler.search.engine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xiaobaisnack.crawler.config.SiteConfig;
import xiaobaisnack.crawler.storage.dao.PageDao;
import xiaobaisnack.crawler.storage.entity.PageEntity;
import xiaobaisnack.crawler.utils.TimeUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * ⭐ 外部搜索引擎
 *
 * 功能：
 * - 借助外部搜索引擎（百度/Google/自定义）搜索关键词
 * - 提取前10条结果（标题 + URL + 摘要）
 * - 对未抓取过的URL进行二次爬取
 * - 自动存入数据库
 */
public class ExternalSearcher {

    private final SiteConfig siteConfig;
    private final PageDao pageDao;

    public ExternalSearcher(SiteConfig siteConfig) {
        this.siteConfig = siteConfig;
        this.pageDao = new PageDao();
    }

    /**
     * 外部搜索
     * @param keyword 搜索关键词
     * @return 搜索结果列表
     */
    public List<PageEntity> search(String keyword) {
        List<PageEntity> results = new ArrayList<>();

        // 如果没有配置搜索引擎URL，跳过外部搜索
        if (siteConfig.getSearchUrl() == null || siteConfig.getSearchUrl().trim().isEmpty()) {
            System.out.println("[ExternalSearcher] 未配置搜索引擎URL，跳过外部搜索");
            return results;
        }

        try {
            // 1. 拼接搜索URL
            String searchUrl = buildSearchUrl(keyword);
            System.out.println("[ExternalSearcher] 搜索URL: " + searchUrl);

            // 2. 爬取搜索结果页
            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                    .timeout(10000)
                    .get();

            // 3. 提取搜索结果
            List<SearchResultItem> items = extractResults(doc, keyword);
            System.out.println("[ExternalSearcher] 提取到 " + items.size() + " 条外部结果");

            // 4. 对每条结果进行二次爬取并存入数据库
            int count = 0;
            for (SearchResultItem item : items) {
                if (count >= 10) break;

                // 检查是否已存在
                PageEntity existing = pageDao.getByUrl(item.url);
                if (existing != null) {
                    results.add(existing);
                    count++;
                    continue;
                }

                // 二次爬取
                PageEntity page = crawlPage(item);
                if (page != null) {
                    pageDao.save(page);
                    // 重新获取以拿到ID
                    PageEntity saved = pageDao.getByUrl(item.url);
                    if (saved != null) {
                        results.add(saved);
                    } else {
                        results.add(page);
                    }
                    count++;
                }
            }

        } catch (Exception e) {
            System.err.println("[ExternalSearcher] 外部搜索失败: " + e.getMessage());
            e.printStackTrace();
        }

        return results;
    }

    /**
     * 拼接搜索URL
     */
    private String buildSearchUrl(String keyword) {
        String baseUrl = siteConfig.getSearchUrl();
        try {
            String encoded = URLEncoder.encode(keyword, "UTF-8");
            return baseUrl.replace("{keyword}", encoded);
        } catch (Exception e) {
            return baseUrl.replace("{keyword}", keyword);
        }
    }

    /**
     * 从搜索结果页提取结果
     * 支持百度、Google、Bing等常见搜索引擎
     */
    private List<SearchResultItem> extractResults(Document doc, String keyword) {
        List<SearchResultItem> items = new ArrayList<>();
        String baseUrl = siteConfig.getSearchUrl();

        if (baseUrl != null && baseUrl.contains("baidu.com")) {
            // 百度搜索结果提取
            Elements results = doc.select(".result, .c-container");
            for (Element result : results) {
                Element titleEl = result.selectFirst("h3 a, .t a");
                if (titleEl != null) {
                    String title = titleEl.text();
                    String url = titleEl.attr("href");
                    Element abstractEl = result.selectFirst(".c-abstract, .c-span-last span, .c-row");
                    String snippet = abstractEl != null ? abstractEl.text() : "";
                    if (url != null && !url.isEmpty() && url.startsWith("http")) {
                        items.add(new SearchResultItem(title, url, snippet));
                    }
                }
            }
        } else if (baseUrl != null && baseUrl.contains("google.com")) {
            // Google搜索结果提取
            Elements results = doc.select("div.g, div[data-sokoban-container]");
            for (Element result : results) {
                Element titleEl = result.selectFirst("h3");
                Element linkEl = result.selectFirst("a[href^=http]");
                Element snippetEl = result.selectFirst("div.VwiC3b, span.aCOpRe");
                if (titleEl != null && linkEl != null) {
                    String title = titleEl.text();
                    String url = linkEl.attr("href");
                    String snippet = snippetEl != null ? snippetEl.text() : "";
                    if (url.startsWith("http")) {
                        items.add(new SearchResultItem(title, url, snippet));
                    }
                }
            }
        } else if (baseUrl != null && baseUrl.contains("bing.com")) {
            // Bing搜索结果提取
            Elements results = doc.select("li.b_algo");
            for (Element result : results) {
                Element titleEl = result.selectFirst("h2 a");
                Element snippetEl = result.selectFirst(".b_caption p, .b_lineclamp2");
                if (titleEl != null) {
                    String title = titleEl.text();
                    String url = titleEl.attr("href");
                    String snippet = snippetEl != null ? snippetEl.text() : "";
                    if (url != null && !url.isEmpty() && url.startsWith("http")) {
                        items.add(new SearchResultItem(title, url, snippet));
                    }
                }
            }
        } else {
            // 通用提取：尝试提取所有带链接的标题
            Elements links = doc.select("a[href^=http]");
            for (Element link : links) {
                String url = link.attr("href");
                String title = link.text().trim();
                if (title.length() > 5 && url.length() > 10) {
                    items.add(new SearchResultItem(title, url, ""));
                }
            }
        }

        return items;
    }

    /**
     * 二次爬取页面内容
     */
    private PageEntity crawlPage(SearchResultItem item) {
        try {
            System.out.println("[ExternalSearcher] 二次爬取: " + item.url);
            Document doc = Jsoup.connect(item.url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(8000)
                    .get();

            String title = doc.title();
            if (title == null || title.isEmpty()) {
                title = item.title;
            }

            // 提取正文
            String content = doc.body() != null ? doc.body().text() : "";
            // 限制内容长度
            if (content.length() > 5000) {
                content = content.substring(0, 5000);
            }

            PageEntity page = new PageEntity(item.url, title, content, "DONE", TimeUtils.now());
            page.setSource(PageEntity.SOURCE_EXTERNAL);
            return page;

        } catch (Exception e) {
            System.err.println("[ExternalSearcher] 二次爬取失败: " + item.url + " - " + e.getMessage());
            // 抓取失败时不再用摘要代替正文入库，避免污染本地数据
            return null;
        }
    }

    /**
     * 搜索结果项（内部类）
     */
    private static class SearchResultItem {
        String title;
        String url;
        String snippet;

        SearchResultItem(String title, String url, String snippet) {
            this.title = title;
            this.url = url;
            this.snippet = snippet;
        }
    }
}