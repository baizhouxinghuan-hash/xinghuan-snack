package xiaobaisnack.crawler.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ⭐ 时间工具类
 *
 * 功能：
 * - 时间格式化
 * - 时间计算
 * - 性能计时
 */
public class TimeUtils {

    private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String COMPACT_FORMAT = "yyyyMMddHHmmss";

    /**
     * 格式化当前时间
     */
    public static String now() {
        return format(System.currentTimeMillis());
    }

    /**
     * 格式化时间戳
     */
    public static String format(long timestamp) {
        return new SimpleDateFormat(DEFAULT_FORMAT).format(new Date(timestamp));
    }

    /**
     * 紧凑格式时间
     */
    public static String compactNow() {
        return new SimpleDateFormat(COMPACT_FORMAT).format(new Date());
    }

    /**
     * 获取当前时间戳（毫秒）
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 计算耗时（毫秒）
     */
    public static long elapsed(long startTime) {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 格式化耗时
     */
    public static String formatElapsed(long millis) {
        if (millis < 1000) return millis + "ms";
        if (millis < 60000) return String.format("%.1fs", millis / 1000.0);
        if (millis < 3600000) {
            long minutes = millis / 60000;
            long seconds = (millis % 60000) / 1000;
            return minutes + "m " + seconds + "s";
        }
        long hours = millis / 3600000;
        long minutes = (millis % 3600000) / 60000;
        return hours + "h " + minutes + "m";
    }

    /**
     * 睡眠（忽略中断）
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 获取当前时间字符串（用于日志）
     */
    public static String logTime() {
        return "[" + now() + "]";
    }
}