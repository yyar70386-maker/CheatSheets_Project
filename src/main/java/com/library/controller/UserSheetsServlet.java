package com.library.controller;

import com.library.model.Cheatsheets;
import com.library.model.Users;
import com.library.repository.AuditLogRepository;
import com.library.repository.CategoryRepository;
import com.library.repository.CheatsheetRepository;
import com.library.repository.NotificationRepository;
import com.library.repository.UserRepository;
import com.library.util.CategoryResolver;
import com.library.util.JsonUtil;
import com.library.util.SheetContentImages;
import com.library.util.SheetImageUploadHandler;
import com.library.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/my-sheets")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class UserSheetsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final CheatsheetRepository sheetRepo = new CheatsheetRepository();
    private final CategoryRepository catRepo = new CategoryRepository();
    private final AuditLogRepository auditRepo = new AuditLogRepository();
    private final NotificationRepository notiRepo = new NotificationRepository();
    private final UserRepository userRepo = new UserRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Users user = requireUser(request, response);
        if (user == null) {
            return;
        }

        String fetch = request.getParameter("fetch");
        if (fetch != null) {
            serveSheetJson(user, fetch, request, response);
            return;
        }

        WebUtils.prepareUserContext(request);
        request.setAttribute("categoryList", catRepo.findAll());
        var sheets = sheetRepo.findByAuthorId(user.getId());
        String ctx = request.getContextPath();
        for (Cheatsheets s : sheets) {
            s.setContent(SheetContentImages.resolveForDisplay(s.getContent(), ctx));
        }
        request.setAttribute("mySheetList", sheets);
        request.getRequestDispatcher("my_sheets.jsp").forward(request, response);
    }

    private void serveSheetJson(Users user, String fetch, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id;
        try {
            id = Integer.parseInt(fetch);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Cheatsheets sheet = sheetRepo.findById(id);
        if (sheet == null || sheet.getAuthorId() != user.getId()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print("{");
            out.print("\"id\":" + sheet.getId() + ",");
            out.print("\"title\":" + JsonUtil.string(sheet.getTitle()) + ",");
            out.print("\"categoryId\":" + sheet.getCategoryId() + ",");
            String content = SheetContentImages.resolveForDisplay(sheet.getContent(), request.getContextPath());
            out.print("\"content\":" + JsonUtil.string(content));
            out.print("}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Users user = requireUser(request, response);
        if (user == null) {
            return;
        }

        if (isImageUpload(request)) {
            SheetImageUploadHandler.handle(request, response);
            return;
        }

        request.setCharacterEncoding("UTF-8");
        String ctx = request.getContextPath();
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                if (sheetRepo.deleteByIdAndAuthorId(id, user.getId())) {
                    auditRepo.log(user.getId(), "DELETE", "cheatsheets", id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.sendRedirect(ctx + "/my-sheets");
            return;
        }

        String title = trim(request.getParameter("title"));
        String content = request.getParameter("content");
        String categoryParam = request.getParameter("categoryId");
        String customCategory = trim(request.getParameter("customCategoryName"));
        if (title == null || content == null || content.isBlank() || categoryParam == null) {
            request.getSession().setAttribute("sheetError", "Title, category, and content are required.");
            response.sendRedirect(ctx + "/my-sheets");
            return;
        }

        int categoryId = CategoryResolver.resolveCategoryId(categoryParam, customCategory, catRepo);
        if (categoryId < 0) {
            request.getSession().setAttribute("sheetError",
                    CategoryResolver.OTHER_VALUE.equalsIgnoreCase(categoryParam)
                            ? "Please enter a valid custom category name."
                            : "Please select a valid category.");
            response.sendRedirect(ctx + "/my-sheets");
            return;
        }

        Cheatsheets sheet = new Cheatsheets();
        sheet.setTitle(title);
        sheet.setContent(SheetContentImages.persistInlineImages(content, request.getServletContext()));
        sheet.setCategoryId(categoryId);

        if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            Cheatsheets existing = sheetRepo.findById(id);
            if (existing == null || existing.getAuthorId() != user.getId()) {
                response.sendRedirect(ctx + "/my-sheets");
                return;
            }
            sheet.setId(id);
            sheetRepo.update(sheet);
            auditRepo.log(user.getId(), "UPDATE", "cheatsheets", id);
        } else {
            sheet.setAuthorId(user.getId());
            int newId = sheetRepo.save(sheet);
            auditRepo.log(user.getId(), "CREATE", "cheatsheets", newId > 0 ? newId : null);
            if (newId > 0) {
                Users author = userRepo.findById(user.getId());
                String name = author != null ? author.getUsername() : user.getUsername();
                notiRepo.notifyFollowersOfNewSheet(user.getId(), name, sheet.getTitle(), newId);
            }
        }

        response.sendRedirect(ctx + "/my-sheets");
    }

    private static boolean isImageUpload(HttpServletRequest request) {
        return "uploadImage".equals(request.getParameter("action"));
    }

    private static String trim(String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    private Users requireUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Users user = session == null ? null : (Users) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?redirect=my-sheets");
            return null;
        }
        return user;
    }
}
