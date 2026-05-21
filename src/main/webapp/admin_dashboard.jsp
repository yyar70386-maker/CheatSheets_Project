<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.library.model.Users"%>
<%
Users user = (Users) session.getAttribute("user");
if (user == null || user.getRole() != 1) {
    response.sendRedirect("login.jsp");
    return;
}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Admin Dashboard | CheatSheets</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet" href="https://cdn.datatables.net/1.13.8/css/dataTables.bootstrap5.min.css">
<link rel="stylesheet" href="admin.css">
<style>
.card-box {
    background: #fff;
    border: 1px solid var(--border-color);
    border-radius: 12px;
    padding: 24px;
    height: 100%;
}
.stat-value {
    font-size: 30px;
    font-weight: 800;
    margin: 4px 0;
}
.avatar {
    width: 40px;
    height: 40px;
    border-radius: 10px;
    background: var(--primary-color);
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 700;
}
</style>
</head>
<body>
<jsp:include page="sidebar.jsp" />

<div class="main">
    <div class="page-header">
        <div>
            <h4 class="fw-bold mb-1">Dashboard Overview</h4>
            <p class="text-secondary small mb-0">Welcome back, <%= user.getUsername() %>. Here is the current system snapshot.</p>
        </div>
        <div class="d-flex align-items-center gap-3">
            <div class="text-end d-none d-sm-block">
                <div class="fw-semibold small"><%= user.getUsername() %></div>
                <small class="text-muted">Administrator</small>
            </div>
            <div class="avatar"><%= user.getUsername().substring(0, 1).toUpperCase() %></div>
        </div>
    </div>

    <div class="row g-4 mb-4">
        <div class="col-md-3">
            <div class="card-box">
                <small class="text-muted fw-semibold">TOTAL SHEETS</small>
                <div class="stat-value">${stats.total_sheets}</div>
                <a href="manage_sheets" class="small text-decoration-none">Manage sheets</a>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card-box">
                <small class="text-muted fw-semibold">TOTAL USERS</small>
                <div class="stat-value">${stats.total_users}</div>
                <a href="manage_users" class="small text-decoration-none">Manage users</a>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card-box">
                <small class="text-muted fw-semibold">CATEGORIES</small>
                <div class="stat-value">${stats.total_categories}</div>
                <a href="manage_categories" class="small text-decoration-none">Manage categories</a>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card-box">
                <small class="text-muted fw-semibold">COMMENTS</small>
                <div class="stat-value">${stats.total_comments}</div>
                <a href="manage_comments" class="small text-decoration-none">Manage comments</a>
            </div>
        </div>
    </div>

    <div class="table-box">
        <div class="d-flex justify-content-between align-items-center p-4 border-bottom">
            <h6 class="fw-bold m-0">Recent Cheatsheets</h6>
            <a href="manage_sheets" class="btn btn-sm btn-light border px-3">Manage Sheets</a>
        </div>
        <div class="table-responsive">
            <table class="table align-middle admin-data-table">
                <thead>
                    <tr>
                        <th>Title & ID</th>
                        <th>Category</th>
                        <th>Author</th>
                        <th>Status</th>
                        <th class="text-end">Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="sheet" items="${recentSheets}">
                        <tr>
                            <td>
                                <div class="fw-semibold">${sheet.title}</div>
                                <small class="text-muted">#CS-${sheet.id}</small>
                            </td>
                            <td><span class="role-badge role-admin">${sheet.categoryName}</span></td>
                            <td>${empty sheet.authorName ? 'Unknown' : sheet.authorName}</td>
                            <td><span class="text-success small">Active</span></td>
                            <td class="text-end">
                                <a href="manage_sheets" class="btn-action" title="Moderate"><i class="fa-solid fa-shield-halved"></i></a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/1.13.8/js/dataTables.bootstrap5.min.js"></script>
<script src="admin-datatables.js"></script>
</body>
</html>
