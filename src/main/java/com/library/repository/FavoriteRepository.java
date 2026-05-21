package com.library.repository;

import com.library.config.DBConnection;
import com.library.model.Cheatsheets;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavoriteRepository {

    public boolean add(int userId, int sheetId) {
        String sql = "INSERT IGNORE INTO favorites (user_id, sheet_id) VALUES (?, ?)";
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

    public boolean remove(int userId, int sheetId) {
        String sql = "DELETE FROM favorites WHERE user_id = ? AND sheet_id = ?";
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

    public boolean isSaved(int userId, int sheetId) {
        String sql = "SELECT 1 FROM favorites WHERE user_id = ? AND sheet_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, sheetId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Cheatsheets> findByUser(int userId) {
        List<Cheatsheets> list = new ArrayList<>();
        String sql = "SELECT c.*, cat.name AS category_name, u.username AS author_name " +
                     "FROM favorites f " +
                     "JOIN cheatsheets c ON f.sheet_id = c.id " +
                     "LEFT JOIN categories cat ON c.category_id = cat.id " +
                     "LEFT JOIN users u ON c.author_id = u.id " +
                     "WHERE f.user_id = ? ORDER BY f.added_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cheatsheets sheet = new Cheatsheets();
                    sheet.setId(rs.getInt("id"));
                    sheet.setTitle(rs.getString("title"));
                    sheet.setContent(rs.getString("content"));
                    sheet.setCategoryId(rs.getInt("category_id"));
                    sheet.setAuthorId(rs.getInt("author_id"));
                    sheet.setCategoryName(rs.getString("category_name"));
                    sheet.setAuthorName(rs.getString("author_name"));
                    sheet.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(sheet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
