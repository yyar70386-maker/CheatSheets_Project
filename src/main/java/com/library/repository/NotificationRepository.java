package com.library.repository;

import com.library.config.DBConnection;
import com.library.config.SchemaInitializer;
import com.library.model.Notifications;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {

    public NotificationRepository() {
        SchemaInitializer.ensureNotificationColumns();
    }

    public boolean sendNotification(int userId, String message) {
        return sendNotification(userId, message, null, null);
    }

    public boolean sendNotification(int userId, String message, String type, String linkUrl) {
        String sql = "INSERT INTO notifications (user_id, message, is_read, notification_type, link_url) VALUES (?, ?, 0, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, message);
            ps.setString(3, type);
            ps.setString(4, linkUrl);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return sendNotificationLegacy(userId, message);
        }
    }

    private boolean sendNotificationLegacy(int userId, String message) {
        String sql = "INSERT INTO notifications (user_id, message, is_read) VALUES (?, ?, 0)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, message);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void notifyNewFollower(int followedUserId, int followerId, String followerUsername) {
        if (followedUserId == followerId) {
            return;
        }
        String message = followerUsername + " started following you.";
        sendNotification(followedUserId, message, "follow",
                "user?id=" + followerId);
    }

    public void notifySheetReaction(int authorId, int actorId, String actorName,
                                    String reactionType, String sheetTitle, int sheetId) {
        if (authorId == actorId) {
            return;
        }
        String label = formatReactionLabel(reactionType);
        String message = actorName + " reacted with " + label + " on your cheat sheet \"" + sheetTitle + "\"";
        sendNotification(authorId, message, "sheet_reaction", "sheet?id=" + sheetId);
    }

    public void notifyFollowersOfNewSheet(int authorId, String authorName, String sheetTitle, int sheetId) {
        FollowRepository followRepo = new FollowRepository();
        String message = authorName + " published a new cheat sheet: \"" + sheetTitle + "\"";
        String link = "sheet?id=" + sheetId;
        for (int followerId : followRepo.findFollowerIds(authorId)) {
            if (followerId != authorId) {
                sendNotification(followerId, message, "new_sheet", link);
            }
        }
    }

    private static String formatReactionLabel(String reactionType) {
        if (reactionType == null || reactionType.isBlank()) {
            return "a reaction";
        }
        return switch (reactionType.toLowerCase()) {
            case "like" -> "Like";
            case "love" -> "Love";
            case "insightful" -> "Insightful";
            default -> reactionType.substring(0, 1).toUpperCase() + reactionType.substring(1).toLowerCase();
        };
    }

    public List<Notifications> findByUser(int userId, int limit) {
        List<Notifications> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countUnread(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = 0";
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

    public void markAllRead(int userId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Notifications map(ResultSet rs) throws SQLException {
        Notifications n = new Notifications();
        n.setId(rs.getInt("id"));
        n.setUserId(rs.getInt("user_id"));
        n.setMessage(rs.getString("message"));
        n.setRead(rs.getInt("is_read") == 1);
        n.setCreatedAt(rs.getTimestamp("created_at"));
        try {
            n.setType(rs.getString("notification_type"));
            n.setLinkUrl(rs.getString("link_url"));
        } catch (SQLException ignored) {
            // legacy schema without extra columns
        }
        return n;
    }
}
