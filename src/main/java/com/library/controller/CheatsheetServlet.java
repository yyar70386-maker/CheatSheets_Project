package com.library.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import com.library.util.CategoryResolver;
import com.library.util.SheetImageUploadHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.library.model.Cheatsheets;
import com.library.model.Users;
import com.library.repository.AuditLogRepository;
import com.library.repository.CategoryRepository;
import com.library.repository.CheatsheetRepository;
import com.library.util.JsonUtil;
import com.library.util.SheetContentImages;

@WebServlet("/manage_sheets")
// ⚠️ Max Request Size ကို ပုံအကြီးကြီးတွေ ဝင်လာနိုင်တဲ့အတွက် 50MB အထိ တိုးမြှင့်ပေးထားပါတယ်
@MultipartConfig(
    maxFileSize = 50 * 1024 * 1024, 
    maxRequestSize = 60 * 1024 * 1024
)
public class CheatsheetServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final CheatsheetRepository sheetRepo = new CheatsheetRepository();
    private final CategoryRepository categoryRepo = new CategoryRepository();
    private final AuditLogRepository auditRepo = new AuditLogRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (requireAdmin(request, response) == null) {
            return;
        }

        String fetch = request.getParameter("fetch");
        if (fetch != null) {
            serveSheetJson(fetch, request, response);
            return;
        }

        request.setAttribute("sheetList", sheetRepo.findAll());
        request.setAttribute("categoryList", categoryRepo.findAll());
        request.getRequestDispatcher("manage_sheets.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Users admin = requireAdmin(request, response);
        if (admin == null) {
            return;
        }

        request.setCharacterEncoding("UTF-8");
        
        // 🛠️ ပြင်ဆင်ချက်- Multipart Form ဖြစ်သွားတဲ့အတွက် action ကို စိတ်ချရအောင် Part အနေနဲ့ပါ ဖတ်ခိုင်းခြင်း
        String action = request.getParameter("action");
        if (action == null) {
            action = getPartValue(request.getPart("action"));
        }

        if ("uploadImage".equals(action)) {
            SheetImageUploadHandler.handle(request, response);
            return;
        }

        String ctx = request.getContextPath();

        if ("add".equals(action) || "update".equals(action)) {
            handleSave(admin, request, action);
            response.sendRedirect(ctx + "/manage_sheets");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null) {
            idParam = getPartValue(request.getPart("id"));
        }

        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(ctx + "/manage_sheets");
            return;
        }

        Cheatsheets sheet = sheetRepo.findById(id);
        if (sheet == null) {
            response.sendRedirect(ctx + "/manage_sheets");
            return;
        }

        if ("delete".equals(action)) {
            if (sheet.isAdminAuthored() && sheetRepo.delete(id)) {
                auditRepo.log(admin.getId(), "DELETE", "cheatsheets", id);
            }
        } else if ("suspend".equals(action)) {
            if (!sheet.isAdminAuthored() && sheetRepo.updateStatus(id, 0)) {
                auditRepo.log(admin.getId(), "SUSPEND_SHEET", "cheatsheets", id);
            }
        } else if ("activate".equals(action)) {
            if (!sheet.isAdminAuthored() && sheetRepo.updateStatus(id, 1)) {
                auditRepo.log(admin.getId(), "ACTIVATE_SHEET", "cheatsheets", id);
            }
        }

        response.sendRedirect(ctx + "/manage_sheets");
    }

    private void serveSheetJson(String fetch, HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id;
        try {
            id = Integer.parseInt(fetch);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Cheatsheets sheet = sheetRepo.findById(id);
        if (sheet == null || !sheet.isAdminAuthored()) {
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

    private void handleSave(Users admin, HttpServletRequest request, String action) throws IOException, ServletException {
        // 🛠️ ပြင်ဆင်ချက်- ရိုးရိုး Parameter မရပါက Multipart Part ထဲကနေ တန်ဖိုးတွေကို လှမ်းဆွဲထုတ်ခြင်း
        String title = trim(request.getParameter("title"));
        if (title == null) title = trim(getPartValue(request.getPart("title")));

        String content = request.getParameter("content");
        if (content == null) content = getPartValue(request.getPart("content"));

        String categoryParam = request.getParameter("categoryId");
        if (categoryParam == null) categoryParam = getPartValue(request.getPart("categoryId"));

        String customCategory = trim(request.getParameter("customCategoryName"));
        if (customCategory == null) customCategory = trim(getPartValue(request.getPart("customCategoryName")));

        if (title == null || content == null || content.isBlank() || categoryParam == null) {
            System.out.println("❌ Validation Failed: One or more fields are null/empty.");
            return;
        }

        int categoryId = CategoryResolver.resolveCategoryId(categoryParam, customCategory, categoryRepo);
        if (categoryId < 0) {
            return;
        }

        Cheatsheets sheet = new Cheatsheets();
        sheet.setTitle(title);
        sheet.setContent(SheetContentImages.persistInlineImages(content, request.getServletContext()));
        sheet.setCategoryId(categoryId);

        if ("update".equals(action)) {
            String idParam = request.getParameter("id");
            if (idParam == null) idParam = getPartValue(request.getPart("id"));
            
            int id = Integer.parseInt(idParam);
            Cheatsheets existing = sheetRepo.findById(id);
            if (existing == null || !existing.isAdminAuthored()) {
                return;
            }
            sheet.setId(id);
            if (sheetRepo.update(sheet)) {
                auditRepo.log(admin.getId(), "UPDATE", "cheatsheets", id);
            }
        } else {
            sheet.setAuthorId(admin.getId());
            int newId = sheetRepo.save(sheet);
            if (newId > 0) {
                auditRepo.log(admin.getId(), "CREATE", "cheatsheets", newId);
            }
        }
    }

    // 🛠️ Helper Method: Multipart Part ထဲက စာသား String တွေကို UTF-8 နဲ့ သေချာပြောင်းလဲဖတ်ရှုပေးခြင်း
    private String getPartValue(Part part) throws IOException {
        if (part == null) {
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder value = new StringBuilder();
            char[] buffer = new char[1024];
            int length;
            while ((length = reader.read(buffer)) != -1) {
                value.append(buffer, 0, length);
            }
            return value.toString();
        }
    }

    private static String trim(String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    private Users requireAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Users user = session == null ? null : (Users) session.getAttribute("user");
        if (user == null || user.getRole() != Users.ROLE_ADMIN) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return null;
        }
        return user;
    }
}