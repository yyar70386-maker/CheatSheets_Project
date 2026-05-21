package com.library.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.library.config.DBConnection;
import com.library.model.Cheatsheets;

public class AdminRepository {

    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        
        // Table အသီးသီးက အရေအတွက်တွေကို ဆွဲထုတ်မယ့် Query များ
        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM cheatsheets) as total_sheets, " +
                     "(SELECT COUNT(*) FROM users) as total_users, " +
                     "(SELECT COUNT(*) FROM categories) as total_categories, " +
                     "(SELECT COUNT(*) FROM comments) as total_comments";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                stats.put("total_sheets", rs.getInt("total_sheets"));
                stats.put("total_users", rs.getInt("total_users"));
                stats.put("total_categories", rs.getInt("total_categories"));
                stats.put("total_comments", rs.getInt("total_comments"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }

    public List<Cheatsheets> findRecentSheets(int limit) {
        List<Cheatsheets> list = new ArrayList<>();
        String sql = "SELECT c.*, cat.name AS category_name, u.username AS author_name FROM cheatsheets c " +
                     "LEFT JOIN categories cat ON c.category_id = cat.id " +
                     "LEFT JOIN users u ON c.author_id = u.id ORDER BY c.id DESC LIMIT ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cheatsheets sheet = new Cheatsheets();
                    sheet.setId(rs.getInt("id"));
                    sheet.setTitle(rs.getString("title"));
                    sheet.setContent(rs.getString("content"));
                    sheet.setCategoryId(rs.getInt("category_id"));
                    sheet.setCategoryName(rs.getString("category_name"));
                    sheet.setAuthorId(rs.getInt("author_id"));
                    sheet.setDownloadCount(rs.getInt("download_count"));
                    sheet.setCreatedAt(rs.getTimestamp("created_at"));
                    sheet.setAuthorName(rs.getString("author_name"));
                    list.add(sheet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
