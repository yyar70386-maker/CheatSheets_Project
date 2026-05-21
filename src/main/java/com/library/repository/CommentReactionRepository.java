package com.library.repository;

import com.library.config.DBConnection;
import com.library.config.SchemaInitializer;

import java.sql.*;

public class CommentReactionRepository {

    public CommentReactionRepository() {
        SchemaInitializer.ensureSocialSchema();
    }

    public boolean toggle(int userId, int commentId, String reactionType) {
        String existing = getUserReaction(userId, commentId);
        if (existing != null && existing.equals(reactionType)) {
            return remove(userId, commentId);
        }
        String sql = "INSERT INTO comment_reactions (user_id, comment_id, reaction_type) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE reaction_type = VALUES(reaction_type)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, commentId);
            ps.setString(3, reactionType);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean remove(int userId, int commentId) {
        String sql = "DELETE FROM comment_reactions WHERE user_id = ? AND comment_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, commentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUserReaction(int userId, int commentId) {
        String sql = "SELECT reaction_type FROM comment_reactions WHERE user_id = ? AND comment_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, commentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("reaction_type");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int countByComment(int commentId) {
        String sql = "SELECT COUNT(*) FROM comment_reactions WHERE comment_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
