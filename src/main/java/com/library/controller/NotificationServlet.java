package com.library.controller;

import com.library.model.Users;
import com.library.repository.NotificationRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/notifications")
public class NotificationServlet extends HttpServlet {
    private final NotificationRepository notiRepo = new NotificationRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Users user = requireUser(request, response);
        if (user == null) {
            return;
        }
        com.library.util.WebUtils.prepareUserContext(request);
        notiRepo.markAllRead(user.getId());
        request.setAttribute("notifications", notiRepo.findByUser(user.getId(), 50));
        request.setAttribute("unreadNotifications", 0);
        request.getRequestDispatcher("notifications.jsp").forward(request, response);
    }

    private Users requireUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Users user = session == null ? null : (Users) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return null;
        }
        return user;
    }
}
