<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.library.model.Users"%>

<%
    // ၁။ Session ထဲက user object ကို ဆွဲထုတ်တာဖြစ်ပါတယ်။ 
    // ဒါမှ Sidebar မှာ user ရဲ့ နာမည် ဒါမှမဟုတ် icon လေး ပြလို့ရမှာပါ။
    Users user = (Users) session.getAttribute("user");
    
    // ၂။ လက်ရှိ browser ရဲ့ address bar မှာ ပေါ်နေတဲ့ URL ကို စာသားအနေနဲ့ ယူတာပါ။
    // ဒါမှ ဘယ် menu ကို အရောင်လင်းပေးရမလဲ (Active ဖြစ်စေမလဲ) ဆိုတာ သိမှာပါ။
    String uri = request.getRequestURI();
%>

<div class="sidebar">
    <div class="logo">
        <i class="fa-solid fa-layer-group"></i> CheatSheets
    </div>

    <div class="sidebar-content">
        <div class="menu-label">Main</div>
        
        <!-- Dashboard Menu -->
        <a href="admin-dashboard" class="nav-link <%= uri.contains("admin-dashboard") || uri.contains("admin_dashboard.jsp") ? "active" : "" %>">
            <i class="fa-solid fa-chart-pie"></i> Dashboard
        </a>

        <div class="menu-label">Manage</div>
        
        <!-- Cheatsheets Menu: URL ထဲမှာ manage_sheets ပါနေရင် active ဖြစ်နေမယ် -->
        <a href="manage_sheets" class="nav-link <%= uri.contains("manage_sheets") ? "active" : "" %>">
            <i class="fa-solid fa-file-lines"></i> Cheatsheets
        </a>
        
        <!-- Categories Menu -->
        <a href="manage_categories" class="nav-link <%= uri.contains("manage_categories") ? "active" : "" %>">
            <i class="fa-solid fa-shapes"></i> Categories
        </a>
        
        <a href="manage_tags" class="nav-link <%= uri.contains("manage_tags") ? "active" : "" %>">
            <i class="fa-solid fa-tags"></i> Tags
        </a>
        
        <a href="manage_comments" class="nav-link <%= uri.contains("manage_comments") ? "active" : "" %>">
            <i class="fa-solid fa-comments"></i> Comments
        </a>
        
        <a href="manage_users" class="nav-link <%= uri.contains("manage_users") ? "active" : "" %>">
            <i class="fa-solid fa-users"></i> Users
        </a>

        <div class="menu-label">System</div>
        <a href="audit_logs" class="nav-link <%= uri.contains("audit_logs") ? "active" : "" %>">
            <i class="fa-solid fa-clock-rotate-left"></i> Audit Logs
        </a>
    </div>

    <!-- Sign Out Section -->
    <div class="logout-section">
        <a href="logout" class="nav-link text-danger">
            <i class="fa-solid fa-right-from-bracket"></i> Sign Out
        </a>
    </div>
</div>
