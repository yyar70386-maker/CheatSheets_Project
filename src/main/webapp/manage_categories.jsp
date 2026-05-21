<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.library.model.Users"%>

<%
Users user = (Users) session.getAttribute("user");
if(user == null || user.getRole()!=1){
    response.sendRedirect("login.jsp");
    return;
}
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1.0">
<title>Manage Categories | CheatSheets Admin</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet" href="https://cdn.datatables.net/1.13.8/css/dataTables.bootstrap5.min.css">

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

:root {
    --sidebar-bg: #ffffff;
    --main-bg: #f8fafc;
    --primary-color: #4f46e5;
    --text-main: #1e293b;
    --text-muted: #64748b;
    --border-color: #e2e8f0;
}

* { margin: 0; padding: 0; box-sizing: border-box; font-family: 'Inter', sans-serif; }
body { background-color: var(--main-bg); color: var(--text-main); min-height: 100vh; }

/* Sidebar CSS */
.sidebar {
    width: 260px; height: 100vh; position: fixed;
    background: var(--sidebar-bg); border-right: 1px solid var(--border-color);
    display: flex; flex-direction: column; padding-top: 20px; z-index: 1000;
}
.logo { padding: 0 25px 30px; font-size: 20px; font-weight: 700; color: var(--primary-color); display: flex; align-items: center; gap: 10px; }
.sidebar-content { flex: 1; overflow-y: auto; }
.menu-label { padding: 15px 25px 8px; font-size: 11px; font-weight: 600; color: #94a3b8; text-transform: uppercase; letter-spacing: 1px; }
.nav-link { padding: 11px 25px; display: flex; align-items: center; gap: 12px; color: var(--text-muted); font-size: 14px; font-weight: 500; transition: all .2s; text-decoration: none; margin: 0 12px; border-radius: 8px; }
.nav-link:hover { background: #f1f5f9; color: var(--primary-color); }
.nav-link.active { background: #eef2ff; color: var(--primary-color); }
.logout-section { padding: 15px 0; margin-top: auto; border-top: 1px solid var(--border-color); }

/* Main Content Area */
.main { margin-left: 260px; padding: 40px; }
.top-nav { display: flex; justify-content: space-between; align-items: center; margin-bottom: 40px; }
.btn-primary { background-color: var(--primary-color); border: none; padding: 10px 20px; border-radius: 8px; font-weight: 500; }
.table-box { background: #ffffff; border: 1px solid var(--border-color); border-radius: 12px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
.table th { background: #f8fafc; color: var(--text-muted); font-weight: 600; text-transform: uppercase; font-size: 11px; padding: 15px 24px; }
.table td { padding: 18px 24px; border-bottom: 1px solid var(--border-color); font-size: 14px; vertical-align: middle; }
.id-badge { background: #f1f5f9; color: var(--text-muted); padding: 4px 8px; border-radius: 6px; font-family: monospace; font-weight: 600; }
.btn-action { border: 1px solid var(--border-color); background: white; color: var(--text-muted); width: 32px; height: 32px; border-radius: 6px; transition: all 0.2s; }
.btn-action:hover { border-color: var(--primary-color); color: var(--primary-color); background: #f5f3ff; }
.btn-delete-hover:hover { border-color: #ef4444; color: #ef4444; background: #fef2f2; }
</style>
</head>

<body>

<jsp:include page="sidebar.jsp" />

<div class="main">
    <div class="top-nav">
        <div>
            <h4 class="fw-bold mb-1">Categories</h4>
            <p class="text-secondary small mb-0">Manage and organize content categories.</p>
        </div>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#categoryModal" onclick="prepareAdd()">
            <i class="fa-solid fa-plus me-2"></i> New Category
        </button>
    </div>

    <div class="table-box">
        <div class="table-responsive">
            <table class="table admin-data-table">
                <thead>
                    <tr>
                        <th width="80">No</th> <%-- ID နေရာတွင် Serial နံပါတ်အတွက် ခေါင်းစဉ်ပြောင်းထားပါသည် --%>
                        <th>Category Name</th>
                        <th>Description</th>
                        <th width="150" class="text-end">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%-- ပြင်ဆင်ပြီး - Loop ပတ်ရန် varStatus="status" ကို ထည့်သွင်းထားပါသည် --%>
                    <c:forEach var="cat" items="${categoryList}" varStatus="status">
                        <tr>
                            <%-- ပြင်ဆင်ပြီး - Database ID (#${cat.id}) အစား စဉ်တိုက် Serial Number ပြသခြင်း --%>
                            <td><span class="id-badge">${status.count}</span></td>
                            <td class="fw-semibold">${cat.name}</td>
                            <td class="text-secondary">${cat.description}</td>
                            <td class="text-end">
                                <%-- နောက်ကွယ်စနစ်အလုပ်လုပ်ရန် data pass သည့်နေရာတွင် ${cat.id} မူရင်းအတိုင်း ဆက်သုံးထားပါသည် --%>
                                <button class="btn-action" data-bs-toggle="modal" data-bs-target="#categoryModal"
                                    onclick="prepareEdit('${cat.id}', '<c:out value="${cat.name}"/>', '<c:out value="${cat.description}"/>')">
                                    <i class="fa-solid fa-pen"></i>
                                </button>
                                <a href="manage_categories?action=delete&id=${cat.id}" 
                                   onclick="return confirm('Are you sure you want to delete this category?')"
                                   class="btn btn-action btn-delete-hover text-center d-inline-flex align-items-center justify-content-center" 
                                   style="text-decoration: none;">
                                    <i class="fa-solid fa-trash"></i>
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div class="modal fade" id="categoryModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/manage_categories" method="POST">
                <input type="hidden" name="action" id="modalAction" value="insert">
                <input type="hidden" name="id" id="categoryId">
                <div class="modal-header">
                    <h5 class="fw-bold m-0" id="modalTitle">Add New Category</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label small fw-bold text-muted">CATEGORY NAME</label>
                        <input type="text" name="name" id="categoryName" class="form-control" required>
                    </div>
                    <div class="mb-0">
                        <label class="form-label small fw-bold text-muted">DESCRIPTION</label>
                        <textarea name="description" id="categoryDesc" rows="3" class="form-control"></textarea>
                    </div>
                </div>
                <div class="modal-footer border-0 p-4 pt-0">
                    <button type="button" class="btn btn-light border w-100 mb-2" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary w-100">Save Changes</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/1.13.8/js/dataTables.bootstrap5.min.js"></script>
<script src="admin-datatables.js"></script>
<script>
    function prepareAdd() {
        document.getElementById('modalTitle').innerText = "Add New Category";
        document.getElementById('modalAction').value = "insert";
        document.getElementById('categoryId').value = "";
        document.getElementById('categoryName').value = "";
        document.getElementById('categoryDesc').value = "";
    }
    function prepareEdit(id, name, desc) {
        document.getElementById('modalTitle').innerText = "Edit Category";
        document.getElementById('modalAction').value = "update";
        document.getElementById('categoryId').value = id;
        document.getElementById('categoryName').value = name;
        document.getElementById('categoryDesc').value = desc;
    }
</script>
</body>
</html>