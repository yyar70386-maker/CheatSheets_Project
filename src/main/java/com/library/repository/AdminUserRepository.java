package com.library.repository;

import com.library.config.DBConnection;
import com.library.config.SchemaInitializer;
import com.library.model.Users;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class AdminUserRepository {

    public AdminUserRepository() {
        SchemaInitializer.ensureUserAccountColumns();
    }

    public List<Users> findAll() {
        List<Users> list = new ArrayList<>();
        String sql = "SELECT id, username, email, role, account_status, failed_login_attempts FROM users ORDER BY id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        } catch (SQLException e) {
            list.addAll(findAllLegacy());
        }
        return list;
    }

    private List<Users> findAllLegacy() {
        List<Users> list = new ArrayList<>();
        String sql = "SELECT id, username, email, role FROM users ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Users user = new Users();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getInt("role"));
                user.setAccountStatus(Users.STATUS_ACTIVE);
                user.setFailedLoginAttempts(0);
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean save(Users user) {
        String sql = "INSERT INTO users (username, email, password, role, account_status) VALUES (?, ?, ?, ?, 1)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            ps.setInt(4, user.getRole());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Users user) {
        boolean hasPassword = user.getPassword() != null && !user.getPassword().trim().isEmpty();
        String sql = hasPassword
                ? "UPDATE users SET username = ?, email = ?, password = ?, role = ? WHERE id = ?"
                : "UPDATE users SET username = ?, email = ?, role = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            if (hasPassword) {
                ps.setString(3, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
                ps.setInt(4, user.getRole());
                ps.setInt(5, user.getId());
            } else {
                ps.setInt(3, user.getRole());
                ps.setInt(4, user.getId());
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean activateAccount(int userId) {
        return new UserRepository().activateAccount(userId);
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Users mapUser(ResultSet rs) throws SQLException {
        Users user = new Users();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getInt("role"));
        try {
            user.setAccountStatus(rs.getInt("account_status"));
            user.setFailedLoginAttempts(rs.getInt("failed_login_attempts"));
        } catch (SQLException e) {
            user.setAccountStatus(Users.STATUS_ACTIVE);
            user.setFailedLoginAttempts(0);
        }
        return user;
    }
}
