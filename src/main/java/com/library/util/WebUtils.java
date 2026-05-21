package com.library.util;

import com.library.model.Users;
import com.library.repository.NotificationRepository;
import com.library.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public final class WebUtils {
    private WebUtils() {}

    public static void prepareUserContext(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return;
        }
        Users fresh = new UserRepository().findById(user.getId());
        if (fresh != null) {
            if (!fresh.isAdmin() && fresh.isSuspended()) {
                invalidateSuspendedSession(session, request);
                return;
            }
            session.setAttribute("user", SessionUserUtil.forSession(fresh));
            user = fresh;
        }
        request.setAttribute("unreadNotifications", new NotificationRepository().countUnread(user.getId()));
    }

    public static boolean invalidateSuspendedSession(HttpSession session, HttpServletRequest request) {
        session.removeAttribute("user");
        session.setAttribute("authError",
                "Your account has been suspended. Please contact an administrator.");
        try {
            request.getSession().setAttribute("authError",
                    "Your account has been suspended. Please contact an administrator.");
        } catch (Exception ignored) {
        }
        return true;
    }

    public static void redirectIfSuspended(Users user, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (user != null && !user.isAdmin() && user.isSuspended()) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                invalidateSuspendedSession(session, request);
            }
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
}
