package xiaobaisnack.crawler.storage.dao;

import xiaobaisnack.crawler.storage.Database;
import xiaobaisnack.crawler.storage.entity.UrlEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ⭐ URL数据访问对象
 *
 * 功能：
 * - URL表的CRUD操作
 * - 持久化URL状态
 */
public class UrlDao {



    public UrlDao() {
        initTable();
    }

    /**
     * 初始化URL表
     */
    private void initTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS urls (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                url TEXT UNIQUE NOT NULL,
                normalized_url TEXT,
                domain TEXT,
                depth INTEGER DEFAULT 0,
                priority INTEGER DEFAULT 5,
                state TEXT DEFAULT 'NEW',
                create_time INTEGER,
                update_time INTEGER,
                retry_count INTEGER DEFAULT 0,
                error_message TEXT
            );
        """;

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存URL
     */
    public void save(UrlEntity url) {
        String sql = """
            INSERT OR REPLACE INTO urls
            (url, normalized_url, domain, depth, priority, state, create_time, update_time, retry_count, error_message)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, url.getUrl());
            pstmt.setString(2, url.getNormalizedUrl());
            pstmt.setString(3, url.getDomain());
            pstmt.setInt(4, url.getDepth());
            pstmt.setInt(5, url.getPriority());
            pstmt.setString(6, url.getState());
            pstmt.setLong(7, url.getCreateTime());
            pstmt.setLong(8, url.getUpdateTime());
            pstmt.setInt(9, url.getRetryCount());
            pstmt.setString(10, url.getErrorMessage());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新URL状态
     */
    public void updateState(String url, String state) {
        String sql = "UPDATE urls SET state = ?, update_time = ? WHERE url = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, state);
            pstmt.setLong(2, System.currentTimeMillis());
            pstmt.setString(3, url);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据URL查询
     */
    public UrlEntity getByUrl(String url) {
        String sql = "SELECT * FROM urls WHERE url = ? OR normalized_url = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, url);
            pstmt.setString(2, url);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 根据ID查询
     */
    public UrlEntity getById(int id) {
        String sql = "SELECT * FROM urls WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取所有URL
     */
    public List<UrlEntity> getAll() {
        List<UrlEntity> list = new ArrayList<>();
        String sql = "SELECT * FROM urls ORDER BY priority ASC, create_time DESC";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 按状态查询URL
     */
    public List<UrlEntity> getByState(String state) {
        List<UrlEntity> list = new ArrayList<>();
        String sql = "SELECT * FROM urls WHERE state = ? ORDER BY priority ASC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, state);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 按域名查询URL
     */
    public List<UrlEntity> getByDomain(String domain) {
        List<UrlEntity> list = new ArrayList<>();
        String sql = "SELECT * FROM urls WHERE domain = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, domain);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 检查URL是否存在
     */
    public boolean exists(String url) {
        String sql = "SELECT COUNT(*) FROM urls WHERE url = ? OR normalized_url = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, url);
            pstmt.setString(2, url);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取URL总数
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM urls";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 按状态统计数量
     */
    public int countByState(String state) {
        String sql = "SELECT COUNT(*) FROM urls WHERE state = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, state);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 删除URL
     */
    public void delete(String url) {
        String sql = "DELETE FROM urls WHERE url = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, url);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空所有URL
     */
    public void clearAll() {
        String sql = "DELETE FROM urls";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 映射ResultSet到UrlEntity
     */
    private UrlEntity mapResultSet(ResultSet rs) throws SQLException {
        UrlEntity entity = new UrlEntity();
        entity.setId(rs.getInt("id"));
        entity.setUrl(rs.getString("url"));
        entity.setState(rs.getString("state"));
        entity.setDepth(rs.getInt("depth"));
        entity.setPriority(rs.getInt("priority"));
        entity.setCreateTime(rs.getLong("create_time"));
        entity.setUpdateTime(rs.getLong("update_time"));
        entity.setRetryCount(rs.getInt("retry_count"));
        entity.setErrorMessage(rs.getString("error_message"));
        return entity;
    }
}