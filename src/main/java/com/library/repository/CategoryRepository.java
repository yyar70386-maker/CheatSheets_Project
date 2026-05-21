package com.library.repository;

import com.library.config.DBConnection;
import com.library.model.Categories;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

	public List<Categories> findAll() {
	    List<Categories> list = new ArrayList<>();
	    // SQL Query တွင် ORDER BY id DESC ထည့်၍ အစဉ်လိုက်စီခြင်း
	    String sql = "SELECT id, name, description FROM categories ORDER BY name ASC";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            Categories cat = new Categories();
	            cat.setId(rs.getInt("id"));
	            cat.setName(rs.getString("name"));
	            cat.setDescription(rs.getString("description"));
	            list.add(cat);
	        }
	    } catch (SQLException e) {
	        System.err.println("Database Error: " + e.getMessage());
	    }
	    return list;
	}

    public Categories findByName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String sql = "SELECT id, name, description FROM categories WHERE LOWER(name) = LOWER(?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Categories cat = new Categories();
                    cat.setId(rs.getInt("id"));
                    cat.setName(rs.getString("name"));
                    cat.setDescription(rs.getString("description"));
                    return cat;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Finds existing category by name (case-insensitive) or inserts a new one. Returns -1 on failure. */
    public int findOrCreateByName(String name) {
        if (name == null || name.isBlank()) {
            return -1;
        }
        String trimmed = name.trim();
        Categories existing = findByName(trimmed);
        if (existing != null) {
            return existing.getId();
        }
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, trimmed);
            ps.setString(2, "Custom category");
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            Categories again = findByName(trimmed);
            if (again != null) {
                return again.getId();
            }
            e.printStackTrace();
        }
        return -1;
    }

    public Categories findById(int id) {
        String sql = "SELECT id, name, description FROM categories WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Categories cat = new Categories();
                    cat.setId(rs.getInt("id"));
                    cat.setName(rs.getString("name"));
                    cat.setDescription(rs.getString("description"));
                    return cat;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int countSheets(int categoryId) {
        String sql = "SELECT COUNT(*) FROM cheatsheets WHERE category_id = ? AND COALESCE(status, 1) = 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            try {
                String fallback = "SELECT COUNT(*) FROM cheatsheets WHERE category_id = ?";
                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(fallback)) {
                    ps.setInt(1, categoryId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return rs.getInt(1);
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return 0;
    }
    
    // Category အသစ်ထည့်ရန် (Save Method)
    public boolean save(Categories cat) {
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cat.getName());
            ps.setString(2, cat.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
 // Category ဖျက်ရန်
    public boolean delete(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Category ပြင်ရန်
    public boolean update(Categories cat) {
        String sql = "UPDATE categories SET name = ?, description = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cat.getName());
            ps.setString(2, cat.getDescription());
            ps.setInt(3, cat.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}