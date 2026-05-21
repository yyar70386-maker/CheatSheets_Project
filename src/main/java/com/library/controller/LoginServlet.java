package com.library.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.library.model.Users;
import com.library.repository.UserRepository;
import com.library.util.AuthenticationResult;
import com.library.util.SessionUserUtil;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserRepository userRepo = new UserRepository();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String pass = request.getParameter("password");

        HttpSession session = request.getSession();
        String path = request.getContextPath();

        if ("admin@gmail.com".equals(email) && "admin@1234".equals(pass)) {
            Users admin = userRepo.findById(1);
            if (admin == null) {
                admin = new Users();
                admin.setId(1);
                admin.setUsername("Admin");
                admin.setEmail("admin@gmail.com");
                admin.setRole(Users.ROLE_ADMIN);
                admin.setAccountStatus(Users.STATUS_ACTIVE);
            }
            session.setAttribute("user", SessionUserUtil.forSession(admin));
            response.sendRedirect(path + "/admin-dashboard");
            return;
        }

        AuthenticationResult result = userRepo.authenticate(email, pass);

        switch (result.getStatus()) {
            case SUCCESS -> {
                Users user = result.getUser();
                session.setAttribute("user", SessionUserUtil.forSession(user));
                String redirect = request.getParameter("redirect");
                if (redirect != null && !redirect.isBlank() && !redirect.contains("://")) {
                    response.sendRedirect(path + "/" + redirect.replaceFirst("^/", ""));
                } else {
                    response.sendRedirect(path + "/home");
                }
            }
            case SUSPENDED -> {
                session.setAttribute("authError",
                        "Your account is suspended. Please contact an administrator.");
                response.sendRedirect(path + "/login.jsp");
            }
            case LOCKED -> {
                session.setAttribute("authError",
                        "Your account has been locked after 5 failed login attempts. An administrator must reactivate it.");
                response.sendRedirect(path + "/login.jsp");
            }
            case INVALID_PASSWORD -> {
                if (result.getAttemptsRemaining() > 0) {
                    session.setAttribute("authError",
                            "Invalid email or password. " + result.getAttemptsRemaining() + " attempt(s) remaining before lockout.");
                } else {
                    session.setAttribute("authError", "Invalid email or password.");
                }
                response.sendRedirect(path + "/login.jsp");
            }
            default -> {
                session.setAttribute("authError", "Invalid email or password!");
                response.sendRedirect(path + "/login.jsp");
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}
