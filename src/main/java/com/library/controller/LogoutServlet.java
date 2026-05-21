package com.library.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.library.model.Users;
import com.library.repository.AuditLogRepository;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ၁. လက်ရှိ Session ကို ယူမယ်
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            Users user = (Users) session.getAttribute("user");
            if (user != null) {
                new AuditLogRepository().log(user.getId(), "SIGN_OUT", "users", user.getId());
            }
            // ၂. Session ထဲက data တွေကို အကုန်ဖျက်မယ်
            session.invalidate();
        }
        
        // ၃. Home page ကို ပြန်ပို့မယ်
        response.sendRedirect(request.getContextPath() + "/home");
    }
}
