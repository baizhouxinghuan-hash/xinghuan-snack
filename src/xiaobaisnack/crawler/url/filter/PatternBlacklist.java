package xiaobaisnack.crawler.url.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PatternBlacklist {

    private static final List<Pattern> patterns = new ArrayList<>();

    static {
        // 🚫 搜索页（会无限扩散）
        patterns.add(Pattern.compile(".*[?&]q=.*"));
        patterns.add(Pattern.compile(".*search.*"));

        // 🚫 翻页陷阱
        patterns.add(Pattern.compile(".*page=\\d+.*"));

        // 🚫 带 session/token
        patterns.add(Pattern.compile(".*session.*"));
        patterns.add(Pattern.compile(".*token=.*"));
    }

    public static boolean isBlocked(String url) {
        for (Pattern p : patterns) {
            if (p.matcher(url).matches()) {
                return true;
            }
        }
        return false;
    }
}