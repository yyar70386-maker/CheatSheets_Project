package com.library.repository;

import com.library.config.DBConnection;
import com.library.model.Comments;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentRepository {

    public List<Comments> findAll() {
        ensureParentColumn();
        List<Comments> list = new ArrayList<>();
        // status column ကိုပါ SELECT ထဲမှာ ဆွဲထုတ်ထားပါတယ်
        String sql = "SELECT c.*, u.username, s.title AS sheet_title FROM comments c " +
                     "LEFT JOIN users u ON c.user_id = u.id " +
                     "LEFT JOIN cheatsheets s ON c.sheet_id = s.id ORDER BY c.id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapComment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Comments> findBySheet(int sheetId, Integer viewerUserId) {
        ensureParentColumn();
        com.library.config.SchemaInitializer.ensureSocialSchema();
        List<Comments> list = new ArrayList<>();
        // User ဘက်တွင် ပြသမည့်အခါ status = 1 (Active) ဖြစ်သော comment များကိုသာ ပြသမည်
        String sql = "SELECT c.*, u.username, u.avatar_path AS user_avatar, s.title AS sheet_title, " +
                     "pu.username AS parent_username, " +
                     "(SELECT COUNT(*) FROM comment_reactions cr WHERE cr.comment_id = c.id) AS like_count, " +
                     "(SELECT cr.reaction_type FROM comment_reactions cr WHERE cr.comment_id = c.id AND cr.user_id = ? LIMIT 1) AS user_reaction " +
                     "FROM comments c " +
                     "LEFT JOIN users u ON c.user_id = u.id " +
                     "LEFT JOIN cheatsheets s ON c.sheet_id = s.id " +
                     "LEFT JOIN comments parent ON c.parent_id = parent.id " +
                     "LEFT JOIN users pu ON parent.user_id = pu.id " +
                     "WHERE c.sheet_id = ? AND c.status = 1 " +
                     "ORDER BY COALESCE(c.parent_id, c.id) DESC, c.parent_id IS NOT NULL, c.id ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int viewer = viewerUserId == null ? -1 : viewerUserId;
            ps.setInt(1, viewer);
            ps.setInt(2, sheetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapComment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Comment ကို အပြီးဖျက်မည့်အစား Status ကို 0 (Hide) သို့ ပြောင်းလဲမည့် လုပ်ဆောင်ချက်
    public boolean hideComment(int id) {
        ensureParentColumn();
        String sql = "UPDATE comments SET status = 0 WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean save(Comments comment) {
        ensureParentColumn();
        String sql = "INSERT INTO comments (user_id, sheet_id, parent_id, comment_text, status) VALUES (?, ?, ?, ?, 1)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, comment.getUserId());
            ps.setInt(2, comment.getSheetId());
            setNullableInt(ps, 3, comment.getParentId());
            ps.setString(4, comment.getCommentText());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Comments findById(int id) {
        ensureParentColumn();
        String sql = "SELECT c.*, u.username FROM comments c LEFT JOIN users u ON c.user_id = u.id WHERE c.id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapComment(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateText(int id, int userId, String text) {
        ensureParentColumn();
        String sql = "UPDATE comments SET comment_text = ? WHERE id = ? AND user_id = ? AND status = 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, text);
            ps.setInt(2, id);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Comments comment) {
        ensureParentColumn();
        String sql = "UPDATE comments SET user_id = ?, sheet_id = ?, parent_id = ?, comment_text = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, comment.getUserId());
            ps.setInt(2, comment.getSheetId());
            setNullableInt(ps, 3, comment.getParentId());
            ps.setString(4, comment.getCommentText());
            ps.setInt(5, comment.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        ensureParentColumn();
        deleteReplies(id);
        String sql = "DELETE FROM comments WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void deleteReplies(int parentId) {
        String sql = "DELETE FROM comments WHERE parent_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, parentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Comments mapComment(ResultSet rs) throws SQLException {
        Comments comment = new Comments();
        comment.setId(rs.getInt("id"));
        comment.setUserId(rs.getInt("user_id"));
        comment.setSheetId(rs.getInt("sheet_id"));
        int parentId = rs.getInt("parent_id");
        comment.setParentId(rs.wasNull() ? null : parentId);
        comment.setCommentText(rs.getString("comment_text"));
        comment.setStatus(rs.getInt("status")); // Database မှ status ကို model ထဲထည့်ခြင်း
        comment.setCreatedAt(rs.getTimestamp("created_at"));
        comment.setUsername(rs.getString("username"));
        comment.setSheetTitle(rs.getString("sheet_title"));
        try {
            comment.setParentUsername(rs.getString("parent_username"));
        } catch (SQLException ignored) {}
        try {
            comment.setLikeCount(rs.getInt("like_count"));
        } catch (SQLException ignored) {}
        try {
            comment.setUserReaction(rs.getString("user_reaction"));
        } catch (SQLException ignored) {}
        return comment;
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }

    private void ensureParentColumn() {
        try (Connection con = DBConnection.getConnection()) {
            DatabaseMetaData metaData = con.getMetaData();
            
            // parent_id column စစ်ဆေးခြင်း
            try (ResultSet columns = metaData.getColumns(null, null, "comments", "parent_id")) {
                if (!columns.next()) {
                    try (Statement st = con.createStatement()) {
                        st.execute("ALTER TABLE comments ADD COLUMN parent_id INT NULL AFTER sheet_id");
                        st.execute("CREATE INDEX comments_parent_id_idx ON comments(parent_id)");
                    }
                }
            }
            
            // status column အလိုအလျောက်မရှိပါက ထည့်ပေးခြင်း
            try (ResultSet columns = metaData.getColumns(null, null, "comments", "status")) {
                if (!columns.next()) {
                    try (Statement st = con.createStatement()) {
                        st.execute("ALTER TABLE comments ADD COLUMN status TINYINT DEFAULT 1 AFTER comment_text");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}