package xiaobaisnack.crawler.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库连接工具。
 *
 * <p>统一管理 SQLite 连接串，并开启 WAL / busy_timeout，
 * 降低多线程写入时的 SQLITE_BUSY 概率。</p>
 *
 * <p>数据库路径优先级：-Dcrawler.db 参数 &gt; CRAWLER_DB 环境变量 &gt; 当前目录 crawler.db</p>
 */
public final class Database {

    private static final String DEFAULT_PATH = "crawler.db";

    private Database() {
    }

    /**
     * 返回 JDBC 连接串。
     */
    public static String getJdbcUrl() {
        String path = System.getProperty("crawler.db");
        if (path == null || path.trim().isEmpty()) {
            path = System.getenv("CRAWLER_DB");
        }
        if (path == null || path.trim().isEmpty()) {
            path = DEFAULT_PATH;
        }
        return "jdbc:sqlite:" + path.trim();
    }

    /**
     * 创建一个已配置好的连接。调用方负责关闭。
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(getJdbcUrl());
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA journal_mode=WAL");
            stmt.execute("PRAGMA busy_timeout=5000");
            stmt.execute("PRAGMA synchronous=NORMAL");
        } catch (SQLException ignored) {
            // 部分环境（例如只读介质）不允许修改 PRAGMA，不影响主流程
        }
        return conn;
    }
}