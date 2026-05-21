<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.library.model.Users" %>

<%
    Users user = (Users) session.getAttribute("user");
    if (user == null || user.getRole() != 1) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Manage Cheatsheets | Admin</title>
    <jsp:include page="fragments/admin_head.jsp"/>
    <link href="https://cdn.jsdelivr.net/npm/summernote@0.8.20/dist/summernote-bs5.min.css" rel="stylesheet">
    <style>
        .wrapper { display: flex; width: 100%; }
        .status-active { background: #ecfdf5; color: #059669; padding: 4px 10px; border-radius: 6px; font-size: 0.75rem; font-weight: 600; }
        .status-suspended { background: #fef2f2; color: #dc2626; padding: 4px 10px; border-radius: 6px; font-size: 0.75rem; font-weight: 600; }
        .source-admin { background: #eef2ff; color: #4f46e5; padding: 4px 10px; border-radius: 6px; font-size: 0.75rem; font-weight: 600; }
        .source-user { background: #f1f5f9; color: #475569; padding: 4px 10px; border-radius: 6px; font-size: 0.75rem; font-weight: 600; }
        
        /* Actions Column အတွက် အထူး CSS ကန့်သတ်ချက် */
        .admin-data-table td:last-child {
            white-space: nowrap !important;
            width: 1% !important;
        }
        .dropdown-menu {
            z-index: 1050;
        }
    </style>
</head>
<body>

<div class="wrapper">
    <jsp:include page="sidebar.jsp" />

    <div class="main">
        <div class="d-flex flex-wrap justify-content-between align-items-start gap-3 mb-4">
            <div>
                <h4 class="fw-bold mb-1">Manage all cheat sheets</h4>
                <p class="text-muted small mb-0">
                    <strong>Admin posts</strong> — edit or delete.
                    <strong>User posts</strong> — suspend or restore (no edit/delete).
                </p>
            </div>
            <button type="button" class="btn btn-primary btn-sm shadow-sm" data-bs-toggle="modal" data-bs-target="#adminSheetModal" id="btnNewAdminSheet">
                <i class="fa-solid fa-plus me-1"></i> New admin cheat sheet
            </button>
        </div>

        <div class="table-box">
            <div class="table-responsive" style="overflow-x: auto;">
                <table class="table table-hover align-middle mb-0 admin-data-table">
                    <thead class="text-secondary small text-uppercase">
                        <tr>
                            <th class="ps-4 py-3 border-0">No</th>
                            <th class="border-0">Title</th>
                            <th class="border-0">Author</th>
                            <th class="border-0">Source</th>
                            <th class="border-0">Category</th>
                            <th class="border-0">Status</th>
                            <th class="text-end pe-4 border-0">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="sheet" items="${sheetList}" varStatus="status">
                            <tr>
                                <td class="ps-4 fw-bold text-secondary">${status.count}</td>
                                <td>
                                    <div class="fw-semibold text-dark">${sheet.title}</div>
                                </td>
                                <td class="text-secondary">${empty sheet.authorName ? 'Unknown' : sheet.authorName}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${sheet.adminAuthored}">
                                            <span class="badge source-admin px-2 py-1">Admin</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge source-user px-2 py-1">User</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <span class="badge" style="background:#eef2ff;color:#4f46e5;">${sheet.categoryName}</span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${sheet.status == 0}">
                                            <span class="badge status-suspended px-3 py-2">Suspended</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge status-active px-3 py-2">Active</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                
                                <td class="text-end pe-4">
                                    <div class="dropdown">
                                        <button class="btn btn-sm btn-light border dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                            <i class="fa-solid fa-gear me-1"></i> Actions
                                        </button>
                                        <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0 mt-1">
                                            <li>
                                                <a class="dropdown-item py-2" href="sheet?id=${sheet.id}" target="_blank" rel="noopener">
                                                    <i class="fa-solid fa-eye text-info me-2" style="width: 16px;"></i> View Sheet
                                                </a>
                                            </li>
                                            
                                            <c:choose>
                                                <c:when test="${sheet.adminAuthored}">
                                                    <li>
                                                        <button type="button" class="dropdown-item py-2 edit-admin-sheet"
                                                                data-id="${sheet.id}"
                                                                data-title="<c:out value='${sheet.title}'/>"
                                                                data-category-id="${sheet.categoryId}">
                                                            <i class="fa-solid fa-pen text-primary me-2" style="width: 16px;"></i> Edit Sheet
                                                        </button>
                                                    </li>
                                                    <li><hr class="dropdown-divider my-1"></li>
                                                    <li>
                                                        <form action="manage_sheets" method="post" class="mb-0"
                                                              onsubmit="return confirm('Permanently delete this admin cheat sheet?');">
                                                            <input type="hidden" name="action" value="delete">
                                                            <input type="hidden" name="id" value="${sheet.id}">
                                                            <button type="submit" class="dropdown-item py-2 text-danger">
                                                                <i class="fa-solid fa-trash me-2" style="width: 16px;"></i> Delete Sheet
                                                            </button>
                                                        </form>
                                                    </li>
                                                </c:when>
                                                
                                                <c:otherwise>
                                                    <li><hr class="dropdown-divider my-1"></li>
                                                    <li>
                                                        <c:choose>
                                                            <c:when test="${sheet.status == 0}">
                                                                <form action="manage_sheets" method="post" class="mb-0"
                                                                      onsubmit="return confirm('Restore this cheat sheet for public viewing?');">
                                                                    <input type="hidden" name="action" value="activate">
                                                                    <input type="hidden" name="id" value="${sheet.id}">
                                                                    <button type="submit" class="dropdown-item py-2 text-success">
                                                                        <i class="fa-solid fa-circle-check me-2" style="width: 16px;"></i> Restore Sheet
                                                                    </button>
                                                                </form>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <form action="manage_sheets" method="post" class="mb-0"
                                                                      onsubmit="return confirm('Suspend this user cheat sheet? It will be hidden from browse pages.');">
                                                                    <input type="hidden" name="action" value="suspend">
                                                                    <input type="hidden" name="id" value="${sheet.id}">
                                                                    <button type="submit" class="dropdown-item py-2 text-warning">
                                                                        <i class="fa-solid fa-ban me-2" style="width: 16px;"></i> Suspend Sheet
                                                                    </button>
                                                                </form>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </li>
                                                </c:otherwise>
                                            </c:choose>
                                        </ul>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="adminSheetModal" tabindex="-1">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg">
            <form action="manage_sheets" method="post" id="adminSheetForm" enctype="multipart/form-data">
                <div class="modal-header border-0">
                    <h5 class="fw-bold mb-0" id="adminModalTitle">New admin cheat sheet</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" name="action" id="adminFormAction" value="add">
                    <input type="hidden" name="id" id="adminSheetId">
                    <div class="row g-3">
                        <div class="col-md-8">
                            <label class="form-label small fw-bold">Title</label>
                            <input type="text" name="title" id="adminTitle" class="form-control" required>
                        </div>
                        <div class="col-md-4">
                            <% request.setAttribute("categorySelectId", "adminCategoryId");
                               request.setAttribute("customWrapId", "adminCustomCategoryWrap");
                               request.setAttribute("customInputId", "adminCustomCategoryName"); %>
                            <jsp:include page="fragments/sheet_category_fields.jsp"/>
                        </div>
                        <div class="col-12">
                            <label class="form-label small fw-bold">Content</label>
                            <textarea name="content" id="adminSummernote"></textarea>
                        </div>
                    </div>
                </div>
                <div class="modal-footer border-0">
                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/1.13.8/js/dataTables.bootstrap5.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/summernote@0.8.20/dist/summernote-bs5.min.js"></script>
<jsp:include page="fragments/cheat_sheet_editor_init.jsp"/>
<script src="summernote-config.js"></script>
<script src="sheet-form.js"></script>
<script src="admin-datatables.js"></script>
<script>
$(function () {
    initCheatSheetEditor('#adminSummernote');
    bindSheetCategoryOther('adminCategoryId', 'adminCustomCategoryWrap', 'adminCustomCategoryName');

    var modalEl = document.getElementById('adminSheetModal');
    var modal = new bootstrap.Modal(modalEl);

    $('#btnNewAdminSheet').on('click', function () {
        $('#adminModalTitle').text('New admin cheat sheet');
        $('#adminFormAction').val('add');
        $('#adminSheetId').val('');
        $('#adminTitle').val('');
        resetSheetCategory('adminCategoryId', 'adminCustomCategoryWrap', 'adminCustomCategoryName');
        $('#adminSummernote').summernote('code', '');
    });

    $('#adminSheetForm').on('submit', function () {
        if (!validateSheetForm($(this), '#adminSummernote')) {
            return false;
        }
        return true;
    });

    $(document).on('click', '.edit-admin-sheet', function () {
        var id = $(this).data('id');
        $('#adminModalTitle').text('Edit admin cheat sheet');
        $('#adminFormAction').val('update');
        $('#adminSheetId').val(id);
        $('#adminTitle').val($(this).data('title'));
        $('#adminCategoryId').val($(this).data('category-id')).trigger('change');
        $('#adminSummernote').summernote('code', '<p class="text-muted">Loading...</p>');
        modal.show();
        $.getJSON('manage_sheets?fetch=' + id).fail(function () {
            alert('Could not load cheat sheet content.');
        }).done(function (data) {
            $('#adminSummernote').summernote('code', data.content || '');
        });
    });

    modalEl.addEventListener('hidden.bs.modal', function () {
        if ($('#adminFormAction').val() === 'add') {
            $('#adminSummernote').summernote('code', '');
        }
    });
});
</script>
</body>
</html>