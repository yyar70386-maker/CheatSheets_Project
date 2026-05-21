package com.library.controller;

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

@WebServlet("/manage_users")
public class AdminUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final AdminUserRepository userRepo = new AdminUserRepository();
    private final AuditLogRepository auditRepo = new AuditLogRepository();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Users admin = requireAdmin(request, response);
        if (admin == null) {
            return;
        }

        if ("delete".equals(request.getParameter("action"))) {
            int id = Integer.parseInt(request.getParameter("id"));
            if (id != admin.getId()) {
                userRepo.delete(id);
                auditRepo.log(admin.getId(), "DELETE", "users", id);
            }
            response.sendRedirect("manage_users");
            return;
        }

        request.setAttribute("userList", userRepo.findAll());
        request.getRequestDispatcher("manage_users.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Users admin = requireAdmin(request, response);
        if (admin == null) {
            return;
        }

        String action = request.getParameter("action");

        if ("activate".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            if (userRepo.activateAccount(id)) {
                auditRepo.log(admin.getId(), "ACTIVATE_USER", "users", id);
            }
            response.sendRedirect("manage_users");
            return;
        }

        Users user = new Users();
        user.setUsername(request.getParameter("username"));
        user.setEmail(request.getParameter("email"));
        user.setPassword(request.getParameter("password"));
        user.setRole(Integer.parseInt(request.getParameter("role")));

        if ("update".equals(action)) {
            user.setId(Integer.parseInt(request.getParameter("id")));
            userRepo.update(user);
            auditRepo.log(admin.getId(), "UPDATE", "users", user.getId());
        } else {
            userRepo.save(user);
            auditRepo.log(admin.getId(), "CREATE", "users", null);
        }
        response.sendRedirect("manage_users");
    }

    private Users requireAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Users user = session == null ? null : (Users) session.getAttribute("user");
        if (user == null || user.getRole() != Users.ROLE_ADMIN) {
            response.sendRedirect("login.jsp");
            return null;
        }
        return user;
    }
}
