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
<title>Manage Tags | CheatSheets Admin</title>
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
            <h4 class="fw-bold mb-1">Tags</h4>
            <p class="text-secondary small mb-0">Create and manage searchable cheatsheet tags.</p>
        </div>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#tagModal" onclick="prepareAddTag()">
            <i class="fa-solid fa-plus me-2"></i> New Tag
        </button>
    </div>

    <div class="table-box">
        <div class="table-responsive">
            <table class="table align-middle admin-data-table">
                <thead>
                    <tr>
                        <th width="80">No</th> <%-- ID နေရာတွင် Serial นံပါတ်အတွက် ခေါင်းစဉ် ပြောင်းလဲထားပါသည် --%>
                        <th>Tag Name</th>
                        <th width="150" class="text-end">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%-- ပြင်ဆင်ပြီး - Loop ပတ်ရန် varStatus="status" ကို တိုးထည့်ထားပါသည် --%>
                    <c:forEach var="tag" items="${tagList}" varStatus="status">
                        <tr>
                            <%-- ပြင်ဆင်ပြီး - Database ID (#${tag.id}) အစား စဉ်တိုက် Serial Number ထုတ်ပြခြင်း --%>
                            <td><span class="id-badge">${status.count}</span></td>
                            <td class="fw-semibold">${tag.name}</td>
                            <td class="text-end">
                                <%-- ခလုတ်များတွင် data-id="${tag.id}" မူရင်းအတိုင်း ထည့်သွင်းထားသဖြင့် စနစ်ပုံမှန် အလုပ်လုပ်ပါမည် --%>
                                <button type="button" class="btn-action edit-tag" data-bs-toggle="modal" data-bs-target="#tagModal"
                                        data-id="${tag.id}" data-name="<c:out value='${tag.name}'/>">
                                    <i class="fa-solid fa-pen"></i>
                                </button>
                                <a href="manage_tags?action=delete&id=${tag.id}" class="btn-action text-danger"
                                   onclick="return confirm('Delete this tag?')">
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

<div class="modal fade" id="tagModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <form action="manage_tags" method="post">
                <div class="modal-header">
                    <h5 class="fw-bold m-0" id="tagModalTitle">New Tag</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" name="action" id="tagAction" value="add">
                    <input type="hidden" name="id" id="tagId">
                    <label class="form-label small fw-bold">TAG NAME</label>
                    <input type="text" name="name" id="tagName" class="form-control" required>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Tag</button>
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
function prepareAddTag() {
    $('#tagModalTitle').text('New Tag');
    $('#tagAction').val('add');
    $('#tagId').val('');
    $('#tagName').val('');
}

$('.edit-tag').on('click', function() {
    $('#tagModalTitle').text('Edit Tag');
    $('#tagAction').val('update');
    $('#tagId').val($(this).data('id'));
    $('#tagName').val($(this).data('name'));
});
</script>
</body>
</html>