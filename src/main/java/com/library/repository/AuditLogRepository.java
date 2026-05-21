package com.library.repository;

import com.library.config.DBConnection;
import com.library.model.AuditLogs;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogRepository {

    public List<AuditLogs> findAll() {
        List<AuditLogs> list = new ArrayList<>();
        String sql = "SELECT a.*, u.username FROM audit_logs a " +
                     "LEFT JOIN users u ON a.user_id = u.id ORDER BY a.id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean save(AuditLogs log) {
        String sql = "INSERT INTO audit_logs (user_id, action, entity_name, entity_id) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            setNullableInt(ps, 1, log.getUserId());
            ps.setString(2, log.getAction());
            ps.setString(3, log.getEntityName());
            setNullableInt(ps, 4, log.getEntityId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(AuditLogs log) {
        String sql = "UPDATE audit_logs SET user_id = ?, action = ?, entity_name = ?, entity_id = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            setNullableInt(ps, 1, log.getUserId());
            ps.setString(2, log.getAction());
            ps.setString(3, log.getEntityName());
            setNullableInt(ps, 4, log.getEntityId());
            ps.setInt(5, log.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM audit_logs WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void log(Integer userId, String action, String entityName, Integer entityId) {
        AuditLogs log = new AuditLogs();
        log.setUserId(userId);
        log.setAction(action);
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        save(log);
    }

    private AuditLogs mapRow(ResultSet rs) throws SQLException {
        AuditLogs log = new AuditLogs();
        log.setId(rs.getInt("id"));
        int userId = rs.getInt("user_id");
        log.setUserId(rs.wasNull() ? null : userId);
        log.setUsername(rs.getString("username"));
        log.setAction(rs.getString("action"));
        log.setEntityName(rs.getString("entity_name"));
        int entityId = rs.getInt("entity_id");
        log.setEntityId(rs.wasNull() ? null : entityId);
        log.setCreatedAt(rs.getTimestamp("created_at"));
        return log;
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }
}
