package com.library.repository;

import com.library.config.DBConnection;
import com.library.config.SchemaInitializer;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SheetReactionRepository {

    public enum ToggleResult {
        REMOVED,
        ADDED,
        CHANGED
    }

    public SheetReactionRepository() {
        SchemaInitializer.ensureSocialSchema();
    }

    public ToggleResult toggle(int userId, int sheetId, String reactionType) {
        String existing = getUserReaction(userId, sheetId);
        if (existing != null && existing.equals(reactionType)) {
            remove(userId, sheetId);
            return ToggleResult.REMOVED;
        }
        String sql = "INSERT INTO sheet_reactions (user_id, sheet_id, reaction_type) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE reaction_type = VALUES(reaction_type)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, sheetId);
            ps.setString(3, reactionType);
            if (ps.executeUpdate() <= 0) {
                return ToggleResult.REMOVED;
            }
            return existing == null ? ToggleResult.ADDED : ToggleResult.CHANGED;
        } catch (SQLException e) {
            e.printStackTrace();
            return ToggleResult.REMOVED;
        }
    }

    public boolean remove(int userId, int sheetId) {
        String sql = "DELETE FROM sheet_reactions WHERE user_id = ? AND sheet_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, sheetId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUserReaction(int userId, int sheetId) {
        String sql = "SELECT reaction_type FROM sheet_reactions WHERE user_id = ? AND sheet_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, sheetId);
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

    public int countBySheet(int sheetId) {
        String sql = "SELECT COUNT(*) FROM sheet_reactions WHERE sheet_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, sheetId);
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

    public Map<String, Integer> countByType(int sheetId) {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT reaction_type, COUNT(*) AS cnt FROM sheet_reactions WHERE sheet_id = ? GROUP BY reaction_type";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, sheetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("reaction_type"), rs.getInt("cnt"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}
