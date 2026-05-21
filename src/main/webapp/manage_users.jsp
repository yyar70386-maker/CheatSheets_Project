<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.library.model.Users"%>
<%
Users currentUser = (Users) session.getAttribute("user");
if (currentUser == null || currentUser.getRole() != Users.ROLE_ADMIN) {
    response.sendRedirect("login.jsp");
    return;
}
%>
<!DOCTYPE html>
<html lang="en" data-theme="light">
<head>
    <title>Manage Users | CheatSheets Admin</title>
    <jsp:include page="fragments/admin_head.jsp"/>
</head>
<body>
<jsp:include page="sidebar.jsp"/>

<div class="main">
    <div class="page-header">
        <div>
            <h4 class="page-title mb-1">Users</h4>
           <!--  <p class="text-secondary small mb-0">Role 0 = User, Role 1 = Admin. Unlock suspended accounts after failed logins.</p> -->
        </div>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#userModal" onclick="prepareAddUser()">
            <i class="fa-solid fa-plus me-2"></i> New User
        </button>
    </div>

    <div class="table-box">
        <div class="table-responsive">
            <table class="table align-middle admin-data-table">
                <thead>
                    <tr>
                        <th width="70">No</th>
                        <th>Username</th>
                        <th>Email</th>
                        <th width="100">Role</th>
                        <th width="130">Account</th>
                        <th width="90">Failed</th>
                        <th width="200" class="text-end">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${userList}" varStatus="status">
                        <tr>
                            <td><span class="id-badge">${status.count}</span></td>
                            <td class="fw-semibold">${item.username}</td>
                            <td>${item.email}</td>
                            <td>
                                <span class="role-badge ${item.role == 1 ? 'role-admin' : 'role-user'}">
                                    ${item.role == 1 ? 'Admin' : 'User'}
                                </span>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${item.role == 1}">
                                        <span class="status-badge-active">N/A</span>
                                    </c:when>
                                    <c:when test="${item.suspended}">
                                        <span class="status-badge-suspended">Suspended</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status-badge-active">Active</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-muted small">
                                <c:if test="${item.role == 0}">${item.failedLoginAttempts} / 5</c:if>
                                <c:if test="${item.role == 1}">—</c:if>
                            </td>
                            <td class="text-end">
                                <button type="button" class="btn-action edit-user" data-bs-toggle="modal" data-bs-target="#userModal"
                                        data-id="${item.id}" data-username="<c:out value='${item.username}'/>"
                                        data-email="<c:out value='${item.email}'/>" data-role="${item.role}">
                                    <i class="fa-solid fa-pen"></i>
                                </button>
                                <c:if test="${item.role == 0 && item.suspended && item.id != sessionScope.user.id}">
                                    <form action="manage_users" method="post" class="d-inline"
                                          onsubmit="return confirm('Reactivate this user account?');">
                                        <input type="hidden" name="action" value="activate">
                                        <input type="hidden" name="id" value="${item.id}">
                                        <button type="submit" class="btn-action success border-0" title="Unlock account">
                                            <i class="fa-solid fa-unlock"></i>
                                        </button>
                                    </form>
                                </c:if>
                                <c:if test="${item.id != sessionScope.user.id}">
                                    <a href="manage_users?action=delete&id=${item.id}" class="btn-action text-danger"
                                       onclick="return confirm('Delete this user?')">
                                        <i class="fa-solid fa-trash"></i>
                                    </a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div class="modal fade" id="userModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <form action="manage_users" method="post">
                <div class="modal-header">
                    <h5 class="fw-bold m-0" id="userModalTitle">New User</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" name="action" id="userAction" value="add">
                    <input type="hidden" name="id" id="userId">
                    <div class="mb-3">
                        <label class="form-label small fw-bold">USERNAME</label>
                        <input type="text" name="username" id="username" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">EMAIL</label>
                        <input type="email" name="email" id="email" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">PASSWORD</label>
                        <input type="password" name="password" id="password" class="form-control" required>
                        <small class="text-muted" id="passwordHelp">Required for new users.</small>
                    </div>
                    <div>
                        <label class="form-label small fw-bold">ROLE</label>
                        <select name="role" id="role" class="form-select" required>
                            <option value="0">User (0)</option>
                            <option value="1">Admin (1)</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save User</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="fragments/admin_foot.jsp"/>
<script src="admin-datatables.js"></script>
<script>
function prepareAddUser() {
    $('#userModalTitle').text('New User');
    $('#userAction').val('add');
    $('#userId').val('');
    $('#username').val('');
    $('#email').val('');
    $('#password').val('').prop('required', true);
    $('#passwordHelp').text('Required for new users.');
    $('#role').val('0');
}

$('.edit-user').on('click', function() {
    $('#userModalTitle').text('Edit User');
    $('#userAction').val('update');
    $('#userId').val($(this).data('id'));
    $('#username').val($(this).data('username'));
    $('#email').val($(this).data('email'));
    $('#password').val('').prop('required', false);
    $('#passwordHelp').text('Leave blank to keep the current password.');
    $('#role').val(String($(this).data('role')));
});
</script>
</body>
</html>
