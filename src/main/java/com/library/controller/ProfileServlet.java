package com.library.controller;

import com.library.model.Users;
import com.library.repository.FollowRepository;
import com.library.repository.FavoriteRepository;
import com.library.repository.UserRepository;
import com.library.util.SessionUserUtil;
import com.library.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@WebServlet("/profile")
@MultipartConfig(maxFileSize = 2 * 1024 * 1024)
public class ProfileServlet extends HttpServlet {
    private final UserRepository userRepo = new UserRepository();
    private final FollowRepository followRepo = new FollowRepository();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Users sessionUser = requireUser(request, response);
        if (sessionUser == null) {
            return;
        }
        Users profile = userRepo.findById(sessionUser.getId());
        if (profile == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        WebUtils.prepareUserContext(request);
        loadProfileAttributes(request, profile, sessionUser.getId(), true);
        request.setAttribute("userSheets", new com.library.repository.CheatsheetRepository().findByAuthorId(profile.getId()));
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Users sessionUser = requireUser(request, response);
        if (sessionUser == null) {
            return;
        }
        String ctx = request.getContextPath();
        String action = request.getParameter("action");

        if ("avatar".equals(action)) {
            Part filePart = request.getPart("avatar");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String ext = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.')) : ".png";
                String fileOnly = sessionUser.getId() + "_" + UUID.randomUUID() + ext;
                Path dir = Paths.get(getServletContext().getRealPath("/"), "uploads", "avatars");
                Files.createDirectories(dir);
                Path target = dir.resolve(fileOnly);
                filePart.write(target.toString());
                userRepo.updateAvatarPath(sessionUser.getId(), "uploads/avatars/" + fileOnly);
            }
        } else {
            Users profile = userRepo.findById(sessionUser.getId());
            if (profile != null) {
                profile.setUsername(request.getParameter("username"));
                profile.setBio(request.getParameter("bio"));
                userRepo.updateProfile(profile);
            }
        }

        Users refreshed = userRepo.findById(sessionUser.getId());
        if (refreshed != null) {
            request.getSession().setAttribute("user", SessionUserUtil.forSession(refreshed));
        }
        response.sendRedirect(ctx + "/profile");
    }

    static void loadProfileAttributes(HttpServletRequest request, Users profile, int viewerId, boolean ownProfile) {
        FollowRepository followRepo = new FollowRepository();
        request.setAttribute("profileUser", profile);
        request.setAttribute("ownProfile", ownProfile);
        request.setAttribute("followerCount", followRepo.countFollowers(profile.getId()));
        request.setAttribute("followingCount", followRepo.countFollowing(profile.getId()));
        request.setAttribute("savedSheets", new FavoriteRepository().findByUser(profile.getId()));
        request.setAttribute("followingList", followRepo.findFollowing(profile.getId()));
        request.setAttribute("followersList", followRepo.findFollowers(profile.getId()));
        if (!ownProfile) {
            request.setAttribute("isFollowing", followRepo.isFollowing(viewerId, profile.getId()));
        }
    }

    private Users requireUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Users user = session == null ? null : (Users) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?redirect=profile");
            return null;
        }
        return user;
    }
}
