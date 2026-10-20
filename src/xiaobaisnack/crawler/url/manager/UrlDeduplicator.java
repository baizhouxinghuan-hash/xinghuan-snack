package xiaobaisnack.crawler.url.manager;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UrlDeduplicator {

    private final Set<String> visited = ConcurrentHashMap.newKeySet();

    public boolean isDuplicate(String url) {
        return visited.contains(url);
    }

    public boolean markVisited(String url) {
        return visited.add(url);
    }
}