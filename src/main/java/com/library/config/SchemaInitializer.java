package com.library.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class SchemaInitializer {
    private static volatile boolean initialized;

    private SchemaInitializer() {}

    public static void ensureSocialSchema() {
        if (initialized) {
            return;
        }
        synchronized (SchemaInitializer.class) {
            if (initialized) {
                return;
            }
            try (Connection con = DBConnection.getConnection();
                 Statement st = con.createStatement()) {
                st.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar_path VARCHAR(255) NULL");
                st.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS bio VARCHAR(500) NULL");
            } catch (SQLException e) {
                tryLegacyUserColumns();
            }

            try (Connection con = DBConnection.getConnection();
                 Statement st = con.createStatement()) {
                st.execute("CREATE TABLE IF NOT EXISTS user_follows (" +
                        "follower_id INT NOT NULL, following_id INT NOT NULL, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "PRIMARY KEY (follower_id, following_id), " +
                        "FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE)");

                st.execute("CREATE TABLE IF NOT EXISTS sheet_reactions (" +
                        "user_id INT NOT NULL, sheet_id INT NOT NULL, reaction_type VARCHAR(20) NOT NULL DEFAULT 'like', " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "PRIMARY KEY (user_id, sheet_id), " +
                        "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (sheet_id) REFERENCES cheatsheets(id) ON DELETE CASCADE)");

                st.execute("CREATE TABLE IF NOT EXISTS comment_reactions (" +
                        "user_id INT NOT NULL, comment_id INT NOT NULL, reaction_type VARCHAR(20) NOT NULL DEFAULT 'like', " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "PRIMARY KEY (user_id, comment_id), " +
                        "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE)");

                ensureCheatsheetStatusColumn();
                ensureNotificationColumns();
                ensureUserAccountColumns();
                ensureCheatsheetContentColumn();
                dropAppSettingsTable();
                initialized = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void ensureUserAccountColumns() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            st.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS account_status TINYINT NOT NULL DEFAULT 1");
            st.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS failed_login_attempts INT NOT NULL DEFAULT 0");
        } catch (SQLException e) {
            addColumnIfMissing("users", "account_status", "TINYINT NOT NULL DEFAULT 1");
            addColumnIfMissing("users", "failed_login_attempts", "INT NOT NULL DEFAULT 0");
        }
    }

    public static void ensureNotificationColumns() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            st.execute("ALTER TABLE notifications ADD COLUMN IF NOT EXISTS notification_type VARCHAR(32) NULL");
            st.execute("ALTER TABLE notifications ADD COLUMN IF NOT EXISTS link_url VARCHAR(255) NULL");
        } catch (SQLException e) {
            addColumnIfMissing("notifications", "notification_type", "VARCHAR(32) NULL");
            addColumnIfMissing("notifications", "link_url", "VARCHAR(255) NULL");
        }
    }

    public static void ensureCheatsheetContentColumn() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            st.execute("ALTER TABLE cheatsheets MODIFY COLUMN content LONGTEXT");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void dropAppSettingsTable() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            st.execute("DROP TABLE IF EXISTS app_settings");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void ensureCheatsheetStatusColumn() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            st.execute("ALTER TABLE cheatsheets ADD COLUMN IF NOT EXISTS status TINYINT NOT NULL DEFAULT 1");
        } catch (SQLException e) {
            addColumnIfMissing("cheatsheets", "status", "TINYINT NOT NULL DEFAULT 1");
        }
    }

    private static void tryLegacyUserColumns() {
        addColumnIfMissing("users", "avatar_path", "VARCHAR(255) NULL");
        addColumnIfMissing("users", "bio", "VARCHAR(500) NULL");
    }

    private static void addColumnIfMissing(String table, String column, String definition) {
        try (Connection con = DBConnection.getConnection()) {
            var meta = con.getMetaData();
            try (var cols = meta.getColumns(null, null, table, column)) {
                if (!cols.next()) {
                    try (Statement st = con.createStatement()) {
                        st.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
