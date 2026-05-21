package com.library.util;

import com.library.model.Users;

public final class AvatarUtil {
    private AvatarUtil() {}

    public static boolean hasCustomAvatar(Users user) {
        if (user == null || user.getAvatarPath() == null) {
            return false;
        }
        String path = user.getAvatarPath().trim();
        return !path.isEmpty() && !"null".equalsIgnoreCase(path);
    }

    /** Returns image URL only when the user uploaded a custom avatar; otherwise null. */
    public static String resolveCustomUrl(String contextPath, Users user) {
        if (!hasCustomAvatar(user)) {
            return null;
        }
        String path = user.getAvatarPath().trim();
        if (path.startsWith("http")) {
            return path;
        }
        if (path.startsWith("/")) {
            return contextPath + path;
        }
        return contextPath + "/" + path;
    }

    /** @deprecated Prefer {@link #hasCustomAvatar} + {@link #resolveCustomUrl} with default icon markup. */
    public static String resolveUrl(String contextPath, Users user) {
        String custom = resolveCustomUrl(contextPath, user);
        return custom != null ? custom : contextPath + "/assets/default-avatar.svg";
    }

    public static String resolveUrl(String contextPath, String avatarPath) {
        if (avatarPath != null && !avatarPath.trim().isEmpty() && !"null".equalsIgnoreCase(avatarPath.trim())) {
            if (avatarPath.startsWith("http")) {
                return avatarPath;
            }
            return avatarPath.startsWith("/") ? contextPath + avatarPath : contextPath + "/" + avatarPath;
        }
        return contextPath + "/assets/default-avatar.svg";
    }
}
