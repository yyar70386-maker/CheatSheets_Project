package com.library.repository;

import com.library.config.DBConnection;
import com.library.config.SchemaInitializer;
import com.library.model.Cheatsheets;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CheatsheetRepository {

    // Thread-safe ဖြစ်ပြီး တစ်ကြိမ်ပဲ စစ်ဆေးဖို့အတွက် ကနဦးသတ်မှတ်ခြင်း
    private static Boolean statusColumnPresent = null;

    public CheatsheetRepository() {
        SchemaInitializer.ensureCheatsheetStatusColumn();
        SchemaInitializer.ensureCheatsheetContentColumn();
    }

    /** All sheets for admin moderation (includes suspended). */
    public List<Cheatsheets> findAll() {
        return queryList(
                browseSelectSql() + " ORDER BY c.id DESC",
                null);
    }

    public List<Cheatsheets> findByAuthorId(int authorId) {
        return queryList(
                browseSelectSql() + " WHERE c.author_id = ? ORDER BY c.id DESC",
                ps -> ps.setInt(1, authorId));
    }

    public Cheatsheets findById(int id) {
        List<Cheatsheets> list = queryList(
                browseSelectSql() + " WHERE c.id = ?",
                ps -> ps.setInt(1, id));
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Cheatsheets sheet) {
        String sql = "INSERT INTO cheatsheets (title, content, category_id, author_id) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, sheet.getTitle());
            ps.setString(2, sheet.getContent());
            ps.setInt(3, sheet.getCategoryId());
            ps.setInt(4, sheet.getAuthorId());
            if (ps.executeUpdate() <= 0) {
                return -1;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean update(Cheatsheets sheet) {
        String sql = "UPDATE cheatsheets SET title = ?, content = ?, category_id = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sheet.getTitle());
            ps.setString(2, sheet.getContent());
            ps.setInt(3, sheet.getCategoryId());
            ps.setInt(4, sheet.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatus(int id, int status) {
        if (!hasStatusColumn()) {
            return false;
        }
        String sql = "UPDATE cheatsheets SET status = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Cheatsheets> findByCategoryId(int categoryId) {
        return queryList(
                browseSelectSql() + " WHERE c.category_id = ?" + activeWhereClause() + " ORDER BY c.id DESC",
                ps -> ps.setInt(1, categoryId));
    }

    public List<Cheatsheets> findByCategoryIdAndTagId(int categoryId, int tagId) {
        return queryList(
                browseSelectSql() +
                " INNER JOIN cheatsheet_tags ct ON ct.sheet_id = c.id AND ct.tag_id = ?" +
                " WHERE c.category_id = ?" + activeWhereClause() + " ORDER BY c.id DESC",
                ps -> {
                    ps.setInt(1, tagId);
                    ps.setInt(2, categoryId);
                });
    }

    public List<Cheatsheets> search(String keyword) {
        String pattern = "%" + keyword.trim() + "%";
        return queryList(
                browseSelectSql() +
                " WHERE (c.title LIKE ? OR c.content LIKE ? OR cat.name LIKE ? OR u.username LIKE ?)" +
                activeWhereClause() + " ORDER BY c.id DESC",
                ps -> {
                    ps.setString(1, pattern);
                    ps.setString(2, pattern);
                    ps.setString(3, pattern);
                    ps.setString(4, pattern);
                });
    }

    public List<Cheatsheets> findPopular(int limit) {
        List<Cheatsheets> list = new ArrayList<>();
        String sql = browseSelectSql() + activeWhereClause() + " ORDER BY c.download_count DESC, c.id DESC LIMIT ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSheet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM cheatsheets WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteByIdAndAuthorId(int id, int authorId) {
        String sql = "DELETE FROM cheatsheets WHERE id = ? AND author_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, authorId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String browseSelectSql() {
        return "SELECT c.id, c.title, c.content, c.category_id, c.author_id, c.download_count, c.created_at, " +
               (hasStatusColumn() ? "c.status, " : "") +
               "cat.name AS category_name, u.username AS author_name, u.role AS author_role, " +
               "(SELECT COALESCE(AVG(r.rating), 0) FROM ratings r WHERE r.sheet_id = c.id) AS average_rating, " +
               "(SELECT COUNT(*) FROM ratings r WHERE r.sheet_id = c.id) AS rating_count " +
               "FROM cheatsheets c " +
               "LEFT JOIN categories cat ON c.category_id = cat.id " +
               "LEFT JOIN users u ON c.author_id = u.id ";
    }

    private String activeWhereClause() {
        if (!hasStatusColumn()) {
            return "";
        }
        return " AND COALESCE(c.status, 1) = 1";
    }

    // 🛠️ ပြင်ဆင်ချက် - Connection Pool အချင်းချင်း ညှပ်ပြီး Deadlock မဖြစ်အောင် ပိုမိုမြန်ဆန်ပြီး စိတ်ချရသော စစ်ဆေးမှုပုံစံသို့ ပြောင်းလဲခြင်း
    private boolean hasStatusColumn() {
        if (statusColumnPresent != null) {
            return statusColumnPresent;
        }
        synchronized (CheatsheetRepository.class) {
            if (statusColumnPresent != null) {
                return statusColumnPresent;
            }
            // Metadata အစား ပေါ့ပါးသော Query အား အသုံးပြု၍ စစ်ဆေးခြင်း
            String testSql = "SELECT status FROM cheatsheets LIMIT 1";
            try (Connection con = DBConnection.getConnection();
                 Statement stmt = con.createStatement()) {
                stmt.execute(testSql);
                statusColumnPresent = true;
            } catch (SQLException e) {
                // status column မရှိပါက ဤနေရာသို့ ရောက်လာပါမည်
                statusColumnPresent = false;
            }
            return statusColumnPresent;
        }
    }

    private interface PreparedStatementBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    private List<Cheatsheets> queryList(String sql, PreparedStatementBinder binder) {
        ensureRatingsTable();
        List<Cheatsheets> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (binder != null) {
                binder.bind(ps);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSheet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Cheatsheet query failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    private Cheatsheets mapSheet(ResultSet rs) throws SQLException {
        Cheatsheets sheet = new Cheatsheets();
        sheet.setId(rs.getInt("id"));
        sheet.setTitle(rs.getString("title"));
        sheet.setContent(rs.getString("content"));
        sheet.setCategoryId(rs.getInt("category_id"));
        sheet.setAuthorId(rs.getInt("author_id"));
        sheet.setDownloadCount(rs.getInt("download_count"));
        sheet.setStatus(readStatus(rs));
        sheet.setCreatedAt(rs.getTimestamp("created_at"));
        sheet.setCategoryName(rs.getString("category_name"));
        sheet.setAuthorName(rs.getString("author_name"));
        try {
            sheet.setAuthorRole(rs.getInt("author_role"));
        } catch (SQLException e) {
            sheet.setAuthorRole(0);
        }
        sheet.setAverageRating(rs.getDouble("average_rating"));
        sheet.setRatingCount(rs.getInt("rating_count"));
        return sheet;
    }

    private int readStatus(ResultSet rs) {
        if (!hasStatusColumn()) {
            return 1;
        }
        try {
            int status = rs.getInt("status");
            return rs.wasNull() ? 1 : status;
        } catch (SQLException e) {
            return 1;
        }
    }

    private void ensureRatingsTable() {
        new RatingRepository().ensureTable();
    }
}