package com.library.controller;

import com.library.model.Cheatsheets;
import com.library.model.Comments;
import com.library.model.Ratings;
import com.library.model.Users;
import com.library.repository.*;
import com.library.util.SheetContentImages;
import com.library.util.WebUtils;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/sheet")
public class SheetDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final CheatsheetRepository sheetRepo = new CheatsheetRepository();
    private final CommentRepository commentRepo = new CommentRepository();
    private final RatingRepository ratingRepo = new RatingRepository();
    private final FavoriteRepository favoriteRepo = new FavoriteRepository();
    private final SheetReactionRepository sheetReactionRepo = new SheetReactionRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Cheatsheets sheet = sheetRepo.findById(id);

        if (sheet == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        HttpSession session = request.getSession(false);
        Users user = session == null ? null : (Users) session.getAttribute("user");
        boolean isAdmin = user != null && user.getRole() == 1;

        if (sheet.getStatus() == 0 && !isAdmin) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        Integer viewerId = user == null ? null : user.getId();

        WebUtils.prepareUserContext(request);
        sheet.setContent(SheetContentImages.resolveForDisplay(sheet.getContent(), request.getContextPath()));
        request.setAttribute("sheet", sheet);
        request.setAttribute("commentList", commentRepo.findBySheet(id, viewerId));
        request.setAttribute("ratingList", ratingRepo.findBySheet(id));
        request.setAttribute("sheetReactionCount", sheetReactionRepo.countBySheet(id));
        request.setAttribute("sheetReactionMap", sheetReactionRepo.countByType(id));

        if (user != null) {
            request.setAttribute("saved", favoriteRepo.isSaved(user.getId(), id));
            request.setAttribute("userSheetReaction", sheetReactionRepo.getUserReaction(user.getId(), id));
        }

        request.getRequestDispatcher("sheet_detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Users user = session == null ? null : (Users) session.getAttribute("user");
        int sheetId = Integer.parseInt(request.getParameter("sheetId"));
        String ctx = request.getContextPath();
        String redirect = "sheet?id=" + sheetId;

        if (user == null) {
            response.sendRedirect(ctx + "/login.jsp?redirect=" + redirect);
            return;
        }

        String action = request.getParameter("action");
        if ("comment".equals(action)) {
            Comments comment = new Comments();
            comment.setUserId(user.getId());
            comment.setSheetId(sheetId);
            comment.setParentId(parseNullableInt(request.getParameter("parentId")));
            comment.setCommentText(request.getParameter("commentText"));
            commentRepo.save(comment);
        } else if ("editComment".equals(action)) {
            int commentId = Integer.parseInt(request.getParameter("commentId"));
            commentRepo.updateText(commentId, user.getId(), request.getParameter("commentText"));
        } else if ("rating".equals(action)) {
            Ratings rating = new Ratings();
            rating.setUserId(user.getId());
            rating.setSheetId(sheetId);
            rating.setRating(Integer.parseInt(request.getParameter("rating")));
            ratingRepo.saveOrUpdate(rating);
        }

        response.sendRedirect(ctx + "/" + redirect);
    }

    private Integer parseNullableInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Integer.parseInt(value);
    }
}
