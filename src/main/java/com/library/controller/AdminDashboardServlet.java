package com.library.controller;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.library.repository.AdminRepository;

@WebServlet("/admin-dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private AdminRepository adminRepo = new AdminRepository();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Database ကနေ Stats တွေယူမယ်
        Map<String, Integer> stats = adminRepo.getDashboardStats();
        
        // JSP ဆီကို Data ပို့ပေးမယ်
        request.setAttribute("stats", stats);
        request.setAttribute("recentSheets", adminRepo.findRecentSheets(5));
        
        // Page ကို ကူးပြောင်းမယ်
        request.getRequestDispatcher("admin_dashboard.jsp").forward(request, response);
    }
}
