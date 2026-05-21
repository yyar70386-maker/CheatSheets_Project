package com.library.controller;

import com.library.model.Users;
import com.library.repository.FollowRepository;
import com.library.repository.UserRepository;
import com.library.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/connections")
public class FollowListServlet extends HttpServlet {
    private final UserRepository userRepo = new UserRepository();
    private final FollowRepository followRepo = new FollowRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = parseId(request.getParameter("userId"));
        if (userId < 0) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        Users profile = userRepo.findById(userId);
        if (profile == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        String tab = request.getParameter("tab");
        boolean followingTab = "following".equals(tab);
        if (!followingTab && !"followers".equals(tab)) {
            followingTab = false;
            tab = "followers";
        }

        HttpSession session = request.getSession(false);
        Users viewer = session == null ? null : (Users) session.getAttribute("user");
        boolean ownProfile = viewer != null && viewer.getId() == userId;

        WebUtils.prepareUserContext(request);
        List<Users> connectionList = followingTab
                ? followRepo.findFollowing(userId)
                : followRepo.findFollowers(userId);

        request.setAttribute("profileUser", profile);
        request.setAttribute("connectionList", connectionList);
        request.setAttribute("activeTab", tab);
        request.setAttribute("ownProfile", ownProfile);
        request.setAttribute("followerCount", followRepo.countFollowers(userId));
        request.setAttribute("followingCount", followRepo.countFollowing(userId));
        request.setAttribute("viewerLoggedIn", viewer != null);
        request.getRequestDispatcher("user_connections.jsp").forward(request, response);
    }

    private static int parseId(String value) {
        if (value == null || value.isBlank()) {
            return -1;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
