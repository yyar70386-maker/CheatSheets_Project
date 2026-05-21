package com.library.controller;

import com.library.model.Users;
import com.library.repository.NotificationRepository;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/notifications/unread-count")
public class NotificationCountServlet extends HttpServlet {
    private final NotificationRepository notiRepo = new NotificationRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession(false);
        Users user = session == null ? null : (Users) session.getAttribute("user");
        int count = user == null ? 0 : notiRepo.countUnread(user.getId());
        try (PrintWriter out = response.getWriter()) {
            out.print("{\"count\":");
            out.print(count);
            out.print("}");
        }
    }
}
