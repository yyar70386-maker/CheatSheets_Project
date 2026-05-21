package com.library.controller;

import com.library.model.Users;
import com.library.repository.CheatsheetRepository;
import com.library.repository.UserRepository;
import com.library.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/user")
public class UserProfileServlet extends HttpServlet {
    private final UserRepository userRepo = new UserRepository();
    private final CheatsheetRepository sheetRepo = new CheatsheetRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("id"));
        Users profile = userRepo.findById(userId);
        if (profile == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        HttpSession session = request.getSession(false);
        Users viewer = session == null ? null : (Users) session.getAttribute("user");
        int viewerId = viewer == null ? -1 : viewer.getId();
        boolean ownProfile = viewer != null && viewer.getId() == userId;

        if (ownProfile) {
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        WebUtils.prepareUserContext(request);
        ProfileServlet.loadProfileAttributes(request, profile, viewerId, false);
        request.setAttribute("userSheets", sheetRepo.findByAuthorId(userId));
        request.setAttribute("viewerLoggedIn", viewer != null);
        request.getRequestDispatcher("user_profile.jsp").forward(request, response);
    }
}
