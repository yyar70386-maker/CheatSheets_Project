package com.library.repository;

import com.library.config.DBConnection;
import com.library.config.SchemaInitializer;
import com.library.model.Users;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FollowRepository {

    public FollowRepository() {
        SchemaInitializer.ensureSocialSchema();
    }

    public boolean follow(int followerId, int followingId) {
        if (followerId == followingId) {
            return false;
        }
        String sql = "INSERT IGNORE INTO user_follows (follower_id, following_id) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean unfollow(int followerId, int followingId) {
        String sql = "DELETE FROM user_follows WHERE follower_id = ? AND following_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isFollowing(int followerId, int followingId) {
        String sql = "SELECT 1 FROM user_follows WHERE follower_id = ? AND following_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followingId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countFollowers(int userId) {
        return count("following_id", userId);
    }

    public int countFollowing(int userId) {
        return count("follower_id", userId);
    }

    private int count(String column, int userId) {
        String sql = "SELECT COUNT(*) FROM user_follows WHERE " + column + " = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
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

    public List<Integer> findFollowerIds(int authorId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT follower_id FROM user_follows WHERE following_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, authorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("follower_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    public List<Users> findFollowing(int userId) {
        return findRelated(userId, true);
    }

    public List<Users> findFollowers(int userId) {
        return findRelated(userId, false);
    }

    private List<Users> findRelated(int userId, boolean following) {
        List<Users> list = new ArrayList<>();
        String sql = following
                ? "SELECT u.id, u.username, u.email, u.role, u.avatar_path, u.bio FROM user_follows f " +
                  "JOIN users u ON f.following_id = u.id WHERE f.follower_id = ?"
                : "SELECT u.id, u.username, u.email, u.role, u.avatar_path, u.bio FROM user_follows f " +
                  "JOIN users u ON f.follower_id = u.id WHERE f.following_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Users mapUser(ResultSet rs) throws SQLException {
        Users u = new Users();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setRole(rs.getInt("role"));
        u.setAvatarPath(rs.getString("avatar_path"));
        u.setBio(rs.getString("bio"));
        return u;
    }
}
