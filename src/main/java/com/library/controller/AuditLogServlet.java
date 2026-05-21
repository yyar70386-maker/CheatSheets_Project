package com.library.controller;

import com.library.model.AuditLogs;
import com.library.model.Users;
import com.library.repository.AdminUserRepository;
import com.library.repository.AuditLogRepository;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/audit_logs")
public class AuditLogServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AuditLogRepository auditRepo = new AuditLogRepository();
    private AdminUserRepository userRepo = new AdminUserRepository();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Users admin = requireAdmin(request, response);
        if (admin == null) return;

        if ("delete".equals(request.getParameter("action"))) {
            int id = Integer.parseInt(request.getParameter("id"));
            auditRepo.delete(id);
            response.sendRedirect("audit_logs");
            return;
        }

        request.setAttribute("logList", auditRepo.findAll());
        request.setAttribute("userList", userRepo.findAll());
        request.getRequestDispatcher("audit_logs.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Users admin = requireAdmin(request, response);
        if (admin == null) return;

        AuditLogs log = new AuditLogs();
        log.setUserId(parseNullableInt(request.getParameter("userId")));
        log.setAction(request.getParameter("actionName"));
        log.setEntityName(request.getParameter("entityName"));
        log.setEntityId(parseNullableInt(request.getParameter("entityId")));

        if ("update".equals(request.getParameter("formAction"))) {
            log.setId(Integer.parseInt(request.getParameter("id")));
            auditRepo.update(log);
        } else {
            auditRepo.save(log);
        }
        response.sendRedirect("audit_logs");
    }

    private Integer parseNullableInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Integer.parseInt(value);
    }

    private Users requireAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Users user = session == null ? null : (Users) session.getAttribute("user");
        if (user == null || user.getRole() != 1) {
            response.sendRedirect("login.jsp");
            return null;
        }
        return user;
    }
}
