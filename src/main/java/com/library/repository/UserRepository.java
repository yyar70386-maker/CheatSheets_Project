package com.library.repository;

import com.library.config.DBConnection;
import com.library.config.SchemaInitializer;
import com.library.model.Users;
import com.library.util.AuthenticationResult;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UserRepository {

    public UserRepository() {
        SchemaInitializer.ensureSocialSchema();
        SchemaInitializer.ensureUserAccountColumns();
    }

    public boolean save(Users user) {
        String sql = "INSERT INTO users (username, email, password, role, account_status) VALUES (?, ?, ?, 0, 1)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Duplicate User Error: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public AuthenticationResult authenticate(String email, String password) {
        Users record = findByEmailForAuth(email);
        if (record == null) {
            return AuthenticationResult.notFound();
        }

        if (!record.isAdmin() && record.isSuspended()) {
            return AuthenticationResult.suspended();
        }

        if (!verifyPassword(password, record.getPassword())) {
            if (!record.isAdmin()) {
                int attempts = incrementFailedLogin(record.getId());
                if (attempts >= Users.MAX_FAILED_LOGINS) {
                    suspendAccount(record.getId());
                    return AuthenticationResult.locked();
                }
                int remaining = Users.MAX_FAILED_LOGINS - attempts;
                return AuthenticationResult.invalidPassword(remaining);
            }
            return AuthenticationResult.invalidPassword(0);
        }

        resetFailedLogin(record.getId());
        record.setPassword(null);
        return AuthenticationResult.success(record);
    }

    public Users findById(int id) {
        String sql = "SELECT id, username, email, role, avatar_path, bio, account_status, failed_login_attempts FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            return findByIdLegacy(id);
        }
        return null;
    }

    public Users findByEmail(String email) {
        String sql = "SELECT id, username, email, role, avatar_path, bio, account_status, failed_login_attempts FROM users WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            return findByEmailLegacy(email);
        }
        return null;
    }

    private Users findByEmailForAuth(String email) {
        String sql = "SELECT id, username, email, password, role, avatar_path, bio, account_status, failed_login_attempts FROM users WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs, true);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int incrementFailedLogin(int userId) {
        String sql = "UPDATE users SET failed_login_attempts = failed_login_attempts + 1 WHERE id = ? AND role = 0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getFailedLoginAttempts(userId);
    }

    public void resetFailedLogin(int userId) {
        String sql = "UPDATE users SET failed_login_attempts = 0 WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean suspendAccount(int userId) {
        String sql = "UPDATE users SET account_status = 0 WHERE id = ? AND role = 0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean activateAccount(int userId) {
        String sql = "UPDATE users SET account_status = 1, failed_login_attempts = 0 WHERE id = ? AND role = 0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getFailedLoginAttempts(int userId) {
        String sql = "SELECT failed_login_attempts FROM users WHERE id = ?";
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

    public boolean updateProfile(Users user) {
        String sql = "UPDATE users SET username = ?, bio = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getBio());
            ps.setInt(3, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAvatarPath(int userId, String avatarPath) {
        String sql = "UPDATE users SET avatar_path = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, avatarPath);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean verifyPassword(String plain, String stored) {
        if (stored == null) {
            return false;
        }
        if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
            try {
                return BCrypt.checkpw(plain, stored);
            } catch (Exception e) {
                return false;
            }
        }
        return stored.equals(plain);
    }

    private Users findByIdLegacy(int id) {
        String sql = "SELECT id, username, email, role, avatar_path, bio FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs, false);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Users findByEmailLegacy(String email) {
        String sql = "SELECT id, username, email, role, avatar_path, bio FROM users WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs, false);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Users mapUser(ResultSet rs) throws SQLException {
        return mapUser(rs, false);
    }

    private Users mapUser(ResultSet rs, boolean includePassword) throws SQLException {
        Users user = new Users();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        if (includePassword) {
            user.setPassword(rs.getString("password"));
        }
        user.setRole(rs.getInt("role"));
        user.setAvatarPath(rs.getString("avatar_path"));
        user.setBio(rs.getString("bio"));
        mapSecurityFields(user, rs);
        return user;
    }

    private void mapSecurityFields(Users user, ResultSet rs) {
        try {
            user.setAccountStatus(rs.getInt("account_status"));
            user.setFailedLoginAttempts(rs.getInt("failed_login_attempts"));
        } catch (SQLException e) {
            user.setAccountStatus(Users.STATUS_ACTIVE);
            user.setFailedLoginAttempts(0);
        }
    }
}
