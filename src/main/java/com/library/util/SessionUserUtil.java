package com.library.util;

import com.library.model.Users;

/** Keeps session users as stored in the database; avatar display uses {@link AvatarUtil}. */
public final class SessionUserUtil {
    private SessionUserUtil() {}

    public static Users forSession(Users user) {
        if (user == null) {
            return null;
        }
        if (user.getAvatarPath() != null) {
            String path = user.getAvatarPath().trim();
            if (path.isEmpty() || "null".equalsIgnoreCase(path)) {
                user.setAvatarPath(null);
            }
        }
        return user;
    }
}
