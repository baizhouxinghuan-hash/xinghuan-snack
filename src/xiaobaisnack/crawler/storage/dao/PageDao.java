package xiaobaisnack.crawler.storage.dao;

import xiaobaisnack.crawler.storage.Database;
import xiaobaisnack.crawler.storage.entity.PageEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 页面数据访问对象。
 */
public class PageDao {

    public PageDao() {
        initTable();
    }

    private void initTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS pages (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                url TEXT UNIQUE,
                title TEXT,
                content TEXT,
                status TEXT DEFAULT 'DONE',
                crawl_time TEXT DEFAULT ''
            )
        """;

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            ensureColumn(stmt, "status", "ALTER TABLE pages ADD COLUMN status TEXT DEFAULT 'DONE'");
            ensureColumn(stmt, "crawl_time", "ALTER TABLE pages ADD COLUMN crawl_time TEXT DEFAULT ''");

        } catch (SQLException e) {
            System.err.println("[PageDao] 初始化表失败: " + e.getMessage());
        }
    }

    /**
     * 仅在列缺失时执行 ALTER，避免每次启动都抛出被忽略的异常。
     */
    private void ensureColumn(Statement stmt, String column, String alterSql) {
        boolean exists = false;
        try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(pages)")) {
            while (rs.next()) {
                if (column.equalsIgnoreCase(rs.getString("name"))) {
                    exists = true;
                    break;
                }
            }
        } catch (SQLException e) {
            return;
        }
        if (!exists) {
            try {
                stmt.execute(alterSql);
            } catch (SQLException ignored) {
                // 并发初始化时可能已被其他线程添加
            }
        }
    }

    /**
     * 保存页面：同一 URL 已存在时更新内容，而不是静默忽略。
     */
    public void save(PageEntity page) {
        String sql = """
            INSERT INTO pages(url, title, content, status, crawl_time)
            VALUES(?, ?, ?, ?, ?)
            ON CONFLICT(url) DO UPDATE SET
                title = excluded.title,
                content = excluded.content,
                status = excluded.status,
                crawl_time = excluded.crawl_time
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, page.getUrl());
            pstmt.setString(2, page.getTitle());
            pstmt.setString(3, page.getContent());
            pstmt.setString(4, page.getStatus() != null ? page.getStatus() : "DONE");
            pstmt.setString(5, page.getCrawlTime() != null ? page.getCrawlTime() : "");
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[PageDao] 保存失败: " + page.getUrl() + " - " + e.getMessage());
        }
    }
    /**
     * 关键词搜索：多个关键词之间 AND，单个关键词在 url/title/content 之间 OR。
     * 结果包含 id，便于界面直接编辑、删除。
     */
    public List<PageEntity> search(String keyword) {
        List<PageEntity> result = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }

        String[] tokens = keyword.trim().split("\\s+");
        StringBuilder sql = new StringBuilder(
                "SELECT id, url, title, content, status, crawl_time FROM pages WHERE 1=1");
        for (int i = 0; i < tokens.length; i++) {
            sql.append(" AND (url LIKE ? ESCAPE '\\' OR title LIKE ? ESCAPE '\\' OR content LIKE ? ESCAPE '\\')");
        }
        sql.append(" ORDER BY id DESC LIMIT 200");

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            for (String token : tokens) {
                String kw = "%" + escapeLike(token) + "%";
                pstmt.setString(index++, kw);
                pstmt.setString(index++, kw);
                pstmt.setString(index++, kw);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new PageEntity(
                            rs.getInt("id"),
                            rs.getString("url"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("status"),
                            rs.getString("crawl_time")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("[PageDao] 搜索失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 转义 LIKE 中的通配符，避免用户输入 % 或 _ 时全表命中。
     */
    private String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    public PageEntity getById(int id) {
        String sql = "SELECT id, url, title, content, status, crawl_time FROM pages WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("[PageDao] 查询失败: " + e.getMessage());
        }

        return null;
    }

    public List<PageEntity> getAllPages() {
        List<PageEntity> list = new ArrayList<>();
        String sql = "SELECT id, url, title, content, status, crawl_time FROM pages ORDER BY id DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("[PageDao] 读取列表失败: " + e.getMessage());
        }

        return list;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM pages";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("[PageDao] 统计失败: " + e.getMessage());
        }

        return 0;
    }
    public PageEntity getByUrl(String url) {
        String sql = "SELECT id, url, title, content, status, crawl_time FROM pages WHERE url = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, url);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("[PageDao] 查询失败: " + e.getMessage());
        }

        return null;
    }

    /**
     * 更新页面，返回受影响行数。
     */
    public int update(PageEntity page) {
        String sql = "UPDATE pages SET title = ?, content = ?, status = ?, crawl_time = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, page.getTitle());
            pstmt.setString(2, page.getContent());
            pstmt.setString(3, page.getStatus());
            pstmt.setString(4, page.getCrawlTime());
            pstmt.setInt(5, page.getId());

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[PageDao] 更新失败: " + e.getMessage());
            return 0;
        }
    }

    public int delete(int id) {
        String sql = "DELETE FROM pages WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[PageDao] 删除失败: " + e.getMessage());
            return 0;
        }
    }

    public int deleteByUrl(String url) {
        String sql = "DELETE FROM pages WHERE url = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, url);
            return pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[PageDao] 删除失败: " + e.getMessage());
            return 0;
        }
    }

    public int deleteBatch(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        StringBuilder sql = new StringBuilder("DELETE FROM pages WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append("?");
        }
        sql.append(")");

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < ids.size(); i++) {
                pstmt.setInt(i + 1, ids.get(i));
            }
            return pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[PageDao] 批量删除失败: " + e.getMessage());
            return 0;
        }
    }

    public int deleteAll() {
        String sql = "DELETE FROM pages";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            return stmt.executeUpdate(sql);

        } catch (SQLException e) {
            System.err.println("[PageDao] 清空失败: " + e.getMessage());
            return 0;
        }
    }

    public int getCount() {
        return count();
    }

    private PageEntity mapRow(ResultSet rs) throws SQLException {
        return new PageEntity(
                rs.getInt("id"),
                rs.getString("url"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("status"),
                rs.getString("crawl_time")
        );
    }
}