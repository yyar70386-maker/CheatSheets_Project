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
<title>Audit Logs | CheatSheets Admin</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet" href="https://cdn.datatables.net/1.13.8/css/dataTables.bootstrap5.min.css">
<link rel="stylesheet" href="admin.css">
</head>
<body>
<jsp:include page="sidebar.jsp" />

<div class="main">
    <div class="page-header">
        <div>
            <h4 class="fw-bold mb-1">Audit Logs</h4>
            <p class="text-secondary small mb-0">Track admin activity and maintain system history.</p>
        </div>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#logModal" onclick="prepareAddLog()">
            <i class="fa-solid fa-plus me-2"></i> New Log
        </button>
    </div>

    <div class="table-box">
        <div class="table-responsive">
            <table class="table align-middle admin-data-table">
                <thead>
                    <tr>
                        <th width="80">#</th> <%-- ID နေရာတွင် Serial နံပါတ်အတွက် ခေါင်းစဉ် ပြောင်းလဲထားပါသည် --%>
                        <th>User</th>
                        <th>Action</th>
                        <th>Entity</th>
                        <th width="120">Entity ID</th>
                        <th width="180">Created</th>
                        <th width="150" class="text-end">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%-- ပြင်ဆင်ပြီး - Loop ပတ်ရန် varStatus="status" ကို တိုးထည့်ထားပါသည် --%>
                    <c:forEach var="log" items="${logList}" varStatus="status">
                        <tr>
                            <%-- ပြင်ဆင်ပြီး - Database ID (#${log.id}) အစား စဉ်တိုက် Serial Number ထုတ်ပြခြင်း --%>
                            <td><span class="id-badge">${status.count}</span></td>
                            <td>${empty log.username ? 'System' : log.username}</td>
                            <td class="fw-semibold">${log.action}</td>
                            <td>${log.entityName}</td>
                            <td>${log.entityId}</td>
                            <td class="text-muted small">${log.createdAt}</td>
                            <td class="text-end">
                                <%-- ခလုတ်များနှင့် လင့်ခ်များတွင် မူရင်း Database ID အား ပုံမှန်အတိုင်း ဆက်သုံးထားပါသည် --%>
                                <button type="button" class="btn-action edit-log" data-bs-toggle="modal" data-bs-target="#logModal"
                                        data-id="${log.id}" data-user-id="${log.userId}" data-action-name="<c:out value='${log.action}'/>"
                                        data-entity-name="<c:out value='${log.entityName}'/>" data-entity-id="${log.entityId}">
                                    <i class="fa-solid fa-pen"></i>
                                </button>
                                <a href="audit_logs?action=delete&id=${log.id}" class="btn-action text-danger"
                                   onclick="return confirm('Delete this audit log?')">
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

<div class="modal fade" id="logModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <form action="audit_logs" method="post">
                <div class="modal-header">
                    <h5 class="fw-bold m-0" id="logModalTitle">New Log</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" name="formAction" id="logFormAction" value="add">
                    <input type="hidden" name="id" id="logId">
                    <div class="mb-3">
                        <label class="form-label small fw-bold">USER</label>
                        <select name="userId" id="logUserId" class="form-select">
                            <option value="">System</option>
                            <c:forEach var="item" items="${userList}">
                                <option value="${item.id}">${item.username}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">ACTION</label>
                        <input type="text" name="actionName" id="actionName" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">ENTITY NAME</label>
                        <input type="text" name="entityName" id="entityName" class="form-control">
                    </div>
                    <label class="form-label small fw-bold">ENTITY ID</label>
                    <input type="number" name="entityId" id="entityId" class="form-control">
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Log</button>
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
function prepareAddLog() {
    $('#logModalTitle').text('New Log');
    $('#logFormAction').val('add');
    $('#logId').val('');
    $('#logUserId').val('');
    $('#actionName').val('');
    $('#entityName').val('');
    $('#entityId').val('');
}

$('.edit-log').on('click', function() {
    $('#logModalTitle').text('Edit Log');
    $('#logFormAction').val('update');
    $('#logId').val($(this).data('id'));
    $('#logUserId').val($(this).data('user-id') ? String($(this).data('user-id')) : '');
    $('#actionName').val($(this).data('action-name'));
    $('#entityName').val($(this).data('entity-name'));
    $('#entityId').val($(this).data('entity-id'));
});
</script>
</body>
</html>