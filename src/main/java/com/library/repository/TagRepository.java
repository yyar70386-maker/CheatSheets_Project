package com.library.repository;

import com.library.config.DBConnection;
import com.library.model.Tags;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagRepository {

    public List<Tags> findAll() {
        List<Tags> list = new ArrayList<>();
        String sql = "SELECT id, name FROM tags ORDER BY id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Tags tag = new Tags();
                tag.setId(rs.getInt("id"));
                tag.setName(rs.getString("name"));
                list.add(tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Tags findById(int id) {
        String sql = "SELECT id, name FROM tags WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Tags tag = new Tags();
                    tag.setId(rs.getInt("id"));
                    tag.setName(rs.getString("name"));
                    return tag;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Tags used by at least one cheatsheet in the given category */
    public List<Tags> findByCategoryId(int categoryId) {
        List<Tags> list = new ArrayList<>();
        String sql = "SELECT DISTINCT t.id, t.name FROM tags t " +
                     "INNER JOIN cheatsheet_tags ct ON ct.tag_id = t.id " +
                     "INNER JOIN cheatsheets c ON c.id = ct.sheet_id " +
                     "WHERE c.category_id = ? ORDER BY t.name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tags tag = new Tags();
                    tag.setId(rs.getInt("id"));
                    tag.setName(rs.getString("name"));
                    list.add(tag);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean save(Tags tag) {
        String sql = "INSERT INTO tags (name) VALUES (?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tag.getName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Tags tag) {
        String sql = "UPDATE tags SET name = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tag.getName());
            ps.setInt(2, tag.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM tags WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
