<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.library.model.Users" %>
<%
    Users user = (Users) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?redirect=my-sheets");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en" data-theme="light">
<head>
    <jsp:include page="fragments/theme_init.jsp"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My cheat sheets</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.8/css/dataTables.bootstrap5.min.css">
    <link href="https://cdn.jsdelivr.net/npm/summernote@0.8.20/dist/summernote-bs5.min.css" rel="stylesheet">
    <link rel="stylesheet" href="theme.css">
    <style>
        .content-preview { max-width: 100%; color: #64748b; font-size: 13px; overflow: hidden; text-overflow: ellipsis; }
        .content-preview img { max-width: 48px; max-height: 32px; object-fit: cover; vertical-align: middle; }
    </style>
</head>
<body>

<jsp:include page="fragments/navbar.jsp"/>

<div class="container my-4">
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">
        <div>
            <h1 class="h3 fw-bold mb-1">My cheat sheets</h1>
            <p class="text-muted small mb-0">Create and manage your own cheat sheet posts.</p>
        </div>
        <button type="button" class="btn btn-purple shadow-sm" data-bs-toggle="modal" data-bs-target="#sheetModal" id="btnNewSheet">
            <i class="fa-solid fa-plus me-2"></i>New cheat sheet
        </button>
    </div>

    <c:if test="${not empty sessionScope.sheetError}">
        <div class="alert alert-warning alert-dismissible fade show" role="alert">
            ${sessionScope.sheetError}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% session.removeAttribute("sheetError"); %>
    </c:if>

    <div class="table-shell">
        <div class="table-responsive">
            <table class="table align-middle mb-0" id="mySheetsTable">
                <thead>
                    <tr>
                        <th>Title</th>
                        <th>Category</th>
                        <th>Created</th>
                        <th>Preview</th>
                        <th class="text-end">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="sheet" items="${mySheetList}">
                        <tr>
                            <td>
                                <div class="fw-semibold">${sheet.title}</div>
                                <small class="text-muted">#${sheet.id}</small>
                            </td>
                            <td><span class="badge badge-cat">${sheet.categoryName}</span></td>
                            <td class="text-muted small">${sheet.createdAt}</td>
                            <td><div class="content-preview rich-content">${sheet.content}</div></td>
                            <td class="text-end">
                                <button type="button" class="btn btn-sm btn-outline-primary edit-sheet me-1"
                                        data-id="${sheet.id}"
                                        data-title="<c:out value='${sheet.title}'/>"
                                        data-category-id="${sheet.categoryId}">
                                    <i class="fa-solid fa-pen-to-square"></i>
                                </button>
                                <form action="my-sheets" method="post" class="d-inline" onsubmit="return confirm('Delete this cheat sheet?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="id" value="${sheet.id}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger"><i class="fa-solid fa-trash-can"></i></button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div class="modal fade" id="sheetModal" tabindex="-1">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content shadow-lg border-0">
            <form action="my-sheets" method="post" id="sheetForm" enctype="multipart/form-data">
                <div class="modal-header border-0 pt-4 px-4">
                    <h5 class="fw-bold" id="modalTitle">New cheat sheet</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body px-4">
                    <input type="hidden" name="action" id="formAction" value="add">
                    <input type="hidden" name="id" id="sheetId">
                    <div class="row g-3">
                        <div class="col-md-8">
                            <label class="form-label small fw-bold">Title</label>
                            <input type="text" name="title" id="title" class="form-control bg-light border-0" required placeholder="Title">
                        </div>
                        <div class="col-md-4">
                            <jsp:include page="fragments/sheet_category_fields.jsp"/>
                        </div>
                        <div class="col-12">
                            <label class="form-label small fw-bold">Content</label>
                            <textarea name="content" id="summernote"></textarea>
                        </div>
                    </div>
                </div>
                <div class="modal-footer border-0 pb-4 px-4">
                    <button type="button" class="btn btn-light px-4" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-purple px-4" id="sheetSubmitBtn">Save</button>
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
<script src="app.js"></script>
<script>
$(function () {
    initCheatSheetEditor('#summernote');
    bindSheetCategoryOther('categoryId', 'customCategoryWrap', 'customCategoryName');

    $('#mySheetsTable').DataTable({
        order: [],
        pageLength: 8,
        language: { search: '', searchPlaceholder: 'Filter...' }
    });

    var modalEl = document.getElementById('sheetModal');
    var modal = bootstrap.Modal.getOrCreateInstance(modalEl);

    // 🛠️ ပြင်ဆင်ချက် - New Sheet ဖွင့်လျှင် ချက်ချင်း Focus ပေးပြီးမှ ရှင်းလင်းခြင်း (AddRange Error ကာကွယ်ရန်)
    $('#btnNewSheet').on('click', function () {
        $('#modalTitle').text('New cheat sheet');
        $('#formAction').val('add');
        $('#sheetId').val('');
        $('#title').val('');
        resetSheetCategory('categoryId', 'customCategoryWrap', 'customCategoryName');
        
        modal.show();
        setTimeout(function() {
            $('#summernote').summernote('focus');
            $('#summernote').summernote('code', '');
        }, 200);
    });

    // 🛠️ ပြင်ဆင်ချက် - Edit Sheet နှိပ်လျှင် Modal အရင်ဖွင့်၊ Focus ပေးပြီးမှ ဒေတာထည့်ခြင်း
    $(document).on('click', '.edit-sheet', function () {
        var $btn = $(this);
        var id = $btn.data('id');
        $('#modalTitle').text('Edit cheat sheet');
        $('#formAction').val('update');
        $('#sheetId').val(id);
        $('#title').val($btn.data('title'));
        $('#categoryId').val($btn.data('category-id')).trigger('change');
        
        modal.show();
        
        setTimeout(function() {
            $('#summernote').summernote('focus');
            $('#summernote').summernote('code', '<p class="text-muted">Loading content...</p>');
            
            $.getJSON('my-sheets?fetch=' + id)
                .done(function (data) {
                    $('#summernote').summernote('focus');
                    $('#summernote').summernote('code', data.content || '');
                })
                .fail(function () {
                    alert('Could not load cheat sheet content. Please try again.');
                    modal.hide();
                });
        }, 200);
    });

    // 🛠️ ပြင်ဆင်ချက် - Form Submit မလုပ်မီ အတင်း Focus ပေးပြီး Textarea ထဲသို့ ဒေတာ Sync လုပ်ခြင်း
    $('#sheetForm').on('submit', function () {
        try {
            $('#summernote').summernote('focus');
            var cleanHtml = $('#summernote').summernote('code');
            $('#summernote').val(cleanHtml);
        } catch(e) {
            console.error("Summernote sync error: ", e);
        }

        if (!validateSheetForm($(this), '#summernote')) {
            return false;
        }
        $('#sheetSubmitBtn').prop('disabled', true);
        return true;
    });

    modalEl.addEventListener('hidden.bs.modal', function () {
        $('#sheetSubmitBtn').prop('disabled', false);
    });
});
</script>
</body>
</html>