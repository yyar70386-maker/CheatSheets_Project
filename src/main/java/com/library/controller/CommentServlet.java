package com.library.controller;

import com.library.model.Users;
import com.library.repository.CommentRepository;
import com.library.repository.AuditLogRepository;
import com.library.repository.NotificationRepository;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/manage_comments")
public class CommentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CommentRepository commentRepo = new CommentRepository();
    private AuditLogRepository auditRepo = new AuditLogRepository();
    private NotificationRepository notiRepo = new NotificationRepository();

    // GET: Admin Table ပေါ်တွင် ဒေတာစာရင်းပြသရန်
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Users user = requireAdmin(request, response);
        if (user == null) return;

        request.setAttribute("commentList", commentRepo.findAll());
        request.getRequestDispatcher("manage_comments.jsp").forward(request, response);
    }

    // POST: Comment ကို Hide လုပ်ပြီး User ထံ အကြောင်းပြချက်နှင့် Noti လှမ်းပို့ရန်
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Users admin = requireAdmin(request, response);
        if (admin == null) return;

        if ("hide".equals(request.getParameter("action"))) {
            int commentId = Integer.parseInt(request.getParameter("id"));
            int targetUserId = Integer.parseInt(request.getParameter("userId"));
            String sheetTitle = request.getParameter("sheetTitle");
            String reason = request.getParameter("reason"); // Admin ရွေးလိုက်သည့် အကြောင်းပြချက်

            // 1. Comment ကို DB တွင် ဝှက်လိုက်မည် (Status = 0)
            commentRepo.hideComment(commentId);

            // 2. စနစ်အတွင်း Logic ခြေရာခံရန် Audit Log မှတ်သားမည်
            auditRepo.log(admin.getId(), "HIDE_COMMENT", "comments", commentId);

            // 3. သက်ဆိုင်ရာ User ဆီ Noti ပေးပို့မည်
            String notiMessage = "သင်၏ '" + sheetTitle + "' ကဏ္ဍတွင် ရေးသားခဲ့သော Comment သည် [" + reason + "] ကြောင့် စနစ်မှ ခေတ္တပိတ်ပင် (Hide) ထားခြင်းခံရပါသည်။";
            notiRepo.sendNotification(targetUserId, notiMessage);
        }
        
        response.sendRedirect("manage_comments");
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