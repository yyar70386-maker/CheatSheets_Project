package com.library.controller;

import com.library.model.Cheatsheets;
import com.library.model.Users;
import com.library.repository.CheatsheetRepository;
import com.library.repository.CommentReactionRepository;
import com.library.repository.FavoriteRepository;
import com.library.repository.FollowRepository;
import com.library.repository.NotificationRepository;
import com.library.repository.SheetReactionRepository;
import com.library.repository.SheetReactionRepository.ToggleResult;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/social")
public class SocialActionServlet extends HttpServlet {
    private final FollowRepository followRepo = new FollowRepository();
    private final FavoriteRepository favoriteRepo = new FavoriteRepository();
    private final SheetReactionRepository sheetReactionRepo = new SheetReactionRepository();
    private final CommentReactionRepository commentReactionRepo = new CommentReactionRepository();
    private final NotificationRepository notiRepo = new NotificationRepository();
    private final CheatsheetRepository sheetRepo = new CheatsheetRepository();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Users user = requireUser(request, response);
        if (user == null) {
            return;
        }
        String action = request.getParameter("action");
        String redirect = request.getParameter("redirect");
        if (redirect == null || redirect.isBlank()) {
            redirect = "home";
        }

        switch (action) {
            case "follow" -> handleFollow(user, request);
            case "unfollow" -> followRepo.unfollow(user.getId(), Integer.parseInt(request.getParameter("userId")));
            case "favorite" -> favoriteRepo.add(user.getId(), Integer.parseInt(request.getParameter("sheetId")));
            case "unfavorite" -> favoriteRepo.remove(user.getId(), Integer.parseInt(request.getParameter("sheetId")));
            case "sheetReaction" -> handleSheetReaction(user, request);
            case "commentReaction" -> commentReactionRepo.toggle(user.getId(),
                    Integer.parseInt(request.getParameter("commentId")),
                    request.getParameter("reaction"));
            default -> { }
        }

        response.sendRedirect(request.getContextPath() + "/" + redirect);
    }

    private void handleFollow(Users user, HttpServletRequest request) {
        int targetId = Integer.parseInt(request.getParameter("userId"));
        if (followRepo.follow(user.getId(), targetId)) {
            notiRepo.notifyNewFollower(targetId, user.getId(), user.getUsername());
        }
    }

    private void handleSheetReaction(Users user, HttpServletRequest request) {
        int sheetId = Integer.parseInt(request.getParameter("sheetId"));
        String reaction = request.getParameter("reaction");
        ToggleResult result = sheetReactionRepo.toggle(user.getId(), sheetId, reaction);
        if (result == ToggleResult.REMOVED) {
            return;
        }
        Cheatsheets sheet = sheetRepo.findById(sheetId);
        if (sheet == null) {
            return;
        }
        notiRepo.notifySheetReaction(
                sheet.getAuthorId(),
                user.getId(),
                user.getUsername(),
                reaction,
                sheet.getTitle(),
                sheetId);
    }

    private Users requireUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Users user = session == null ? null : (Users) session.getAttribute("user");
        if (user == null) {
            String redirect = request.getParameter("redirect");
            String q = redirect != null ? "?redirect=" + redirect : "";
            response.sendRedirect(request.getContextPath() + "/login.jsp" + q);
            return null;
        }
        return user;
    }
}
