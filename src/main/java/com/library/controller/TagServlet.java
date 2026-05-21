package com.library.controller;

import com.library.model.Tags;
import com.library.model.Users;
import com.library.repository.AuditLogRepository;
import com.library.repository.TagRepository;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/manage_tags")
public class TagServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TagRepository tagRepo = new TagRepository();
    private AuditLogRepository auditRepo = new AuditLogRepository();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Users user = requireAdmin(request, response);
        if (user == null) return;

        if ("delete".equals(request.getParameter("action"))) {
            int id = Integer.parseInt(request.getParameter("id"));
            tagRepo.delete(id);
            auditRepo.log(user.getId(), "DELETE", "tags", id);
            response.sendRedirect("manage_tags");
            return;
        }

        request.setAttribute("tagList", tagRepo.findAll());
        request.getRequestDispatcher("manage_tags.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Users user = requireAdmin(request, response);
        if (user == null) return;

        Tags tag = new Tags();
        tag.setName(request.getParameter("name"));

        if ("update".equals(request.getParameter("action"))) {
            tag.setId(Integer.parseInt(request.getParameter("id")));
            tagRepo.update(tag);
            auditRepo.log(user.getId(), "UPDATE", "tags", tag.getId());
        } else {
            tagRepo.save(tag);
            auditRepo.log(user.getId(), "CREATE", "tags", null);
        }
        response.sendRedirect("manage_tags");
    }

    private Users requireAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Users user = session == null ? null : (Users) session.getAttribute("user");
        if (user == null || user.getRole() != 1) {
            response.sendRedirect("login.jsp");
            return null;
        }
        return user;
    }
}
