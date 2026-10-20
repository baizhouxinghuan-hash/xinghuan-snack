package xiaobaisnack.crawler.config;

import java.util.HashMap;
import java.util.Map;

/**
 * ⭐ 网站配置类
 *
 * 功能：
 * - 管理不同网站的抓取规则
 * - 自定义每个网站的抓取参数
 */
public class SiteConfig {

    private String domain;
    private int maxDepth = 2;
    private int requestDelay = 2000;
    private boolean allowSubdomains = true;
    private String searchUrl = "";  // 外部搜索引擎URL，如 https://www.baidu.com/s?wd={keyword}
    private Map<String, String> customHeaders = new HashMap<>();
    private Map<String, String> extractRules = new HashMap<>();

    public SiteConfig() {
        // 默认请求头
        customHeaders.put("Accept", "text/html,application/xhtml+xml");
        customHeaders.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
    }

    public SiteConfig(String domain) {
        this();
        this.domain = domain;
    }

    // ===== Getters & Setters =====

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getRequestDelay() {
        return requestDelay;
    }

    public void setRequestDelay(int requestDelay) {
        this.requestDelay = requestDelay;
    }

    public boolean isAllowSubdomains() {
        return allowSubdomains;
    }

    public void setAllowSubdomains(boolean allowSubdomains) {
        this.allowSubdomains = allowSubdomains;
    }

    public Map<String, String> getCustomHeaders() {
        return customHeaders;
    }

    public void addHeader(String key, String value) {
        this.customHeaders.put(key, value);
    }

    public Map<String, String> getExtractRules() {
        return extractRules;
    }

    public void addExtractRule(String field, String cssSelector) {
        this.extractRules.put(field, cssSelector);
    }

    public String getSearchUrl() {
        return searchUrl;
    }

    public void setSearchUrl(String searchUrl) {
        this.searchUrl = searchUrl;
    }

    @Override
    public String toString() {
        return "SiteConfig{" +
                "domain='" + domain + '\'' +
                ", maxDepth=" + maxDepth +
                ", requestDelay=" + requestDelay +
                ", allowSubdomains=" + allowSubdomains +
                '}';
    }
}