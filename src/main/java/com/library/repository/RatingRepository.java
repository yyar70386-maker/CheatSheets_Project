package com.library.repository;

import com.library.config.DBConnection;
import com.library.model.Ratings;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RatingRepository {

    public void ensureTable() {
        String sql = "CREATE TABLE IF NOT EXISTS ratings (" +
                     "id INT NOT NULL AUTO_INCREMENT, " +
                     "user_id INT NOT NULL, " +
                     "sheet_id INT NOT NULL, " +
                     "rating INT NOT NULL, " +
                     "created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP, " +
                     "PRIMARY KEY (id), " +
                     "UNIQUE KEY unique_user_sheet (user_id, sheet_id), " +
                     "KEY sheet_id (sheet_id), " +
                     "CONSTRAINT ratings_user_fk FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                     "CONSTRAINT ratings_sheet_fk FOREIGN KEY (sheet_id) REFERENCES cheatsheets(id) ON DELETE CASCADE" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean saveOrUpdate(Ratings rating) {
        ensureTable();
        String sql = "INSERT INTO ratings (user_id, sheet_id, rating) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE rating = VALUES(rating), created_at = CURRENT_TIMESTAMP";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rating.getUserId());
            ps.setInt(2, rating.getSheetId());
            ps.setInt(3, rating.getRating());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Ratings> findBySheet(int sheetId) {
        ensureTable();
        List<Ratings> list = new ArrayList<>();
        String sql = "SELECT r.*, u.username FROM ratings r LEFT JOIN users u ON r.user_id = u.id " +
                     "WHERE r.sheet_id = ? ORDER BY r.id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, sheetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ratings rating = new Ratings();
                    rating.setId(rs.getInt("id"));
                    rating.setUserId(rs.getInt("user_id"));
                    rating.setSheetId(rs.getInt("sheet_id"));
                    rating.setRating(rs.getInt("rating"));
                    rating.setCreatedAt(rs.getTimestamp("created_at"));
                    rating.setUsername(rs.getString("username"));
                    list.add(rating);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
