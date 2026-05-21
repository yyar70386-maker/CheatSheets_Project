package com.library.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.library.model.Categories;
import com.library.model.Users;
import com.library.repository.AuditLogRepository;
import com.library.repository.CategoryRepository;

@WebServlet("/manage_categories") // ဒီလမ်းကြောင်းက underscore ပါတယ်
public class CategoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CategoryRepository catRepo = new CategoryRepository();
    private AuditLogRepository auditRepo = new AuditLogRepository();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // action က delete ဖြစ်ပြီး id ပါလာမှ ဖျက်မည်
        if ("delete".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                catRepo.delete(id);
                Users user = (Users) request.getSession().getAttribute("user");
                if (user != null) {
                    auditRepo.log(user.getId(), "DELETE", "categories", id);
                }
            }
            // ဖျက်ပြီးရင် underscore ပါတဲ့ manage_categories ကို ပြန်သွားပါ
            response.sendRedirect("manage_categories");
            return; 
        }

        List<Categories> categoryList = catRepo.findAll();
        request.setAttribute("categoryList", categoryList);
        request.getRequestDispatcher("manage_categories.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8"); // မြန်မာစာ support လုပ်ရန်
        String action = request.getParameter("action");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        
        Categories cat = new Categories();
        cat.setName(name);
        cat.setDescription(description);
        
        boolean saved = false;
        if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            cat.setId(id);
            saved = catRepo.update(cat); // Update logic
            Users user = (Users) request.getSession().getAttribute("user");
            if (saved && user != null) {
                auditRepo.log(user.getId(), "UPDATE", "categories", id);
            }
        } else {
            saved = catRepo.save(cat); // Insert logic
            Users user = (Users) request.getSession().getAttribute("user");
            if (saved && user != null) {
                auditRepo.log(user.getId(), "CREATE", "categories", null);
            }
        }
        
        if (saved) {
            // လမ်းကြောင်းကို manage_categories (underscore) သို့ ပြောင်းပါ
            response.sendRedirect("manage_categories");
        } else {
            request.setAttribute("error", "Action failed.");
            doGet(request, response);
        }
    }
}
