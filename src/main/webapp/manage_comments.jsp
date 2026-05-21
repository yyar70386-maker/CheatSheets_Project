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
<title>Review Comments | CheatSheets Admin</title>
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
            <h4 class="fw-bold mb-1">Comments Moderation</h4>
            <p class="text-secondary small mb-0">Hide inappropriate comments and notify users automatically.</p>
        </div>
    </div>

    <div class="table-box mt-4">
        <div class="table-responsive">
            <table class="table align-middle admin-data-table">
                <thead>
                    <tr>
                        <th width="80">No</th> <%-- ID နေရာတွင် Serial နံပါတ်အတွက် ခေါင်းစဉ် ပြောင်းလဲထားပါသည် --%>
                        <th>User</th>
                        <th>Cheatsheet</th>
                        <th>Comment</th>
                        <th width="140">Status</th>
                        <th width="100" class="text-end">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%-- ပြင်ဆင်ပြီး - Loop ပတ်ရန် varStatus="status" ကို တိုးထည့်ထားပါသည် --%>
                    <c:forEach var="comment" items="${commentList}" varStatus="status">
                        <tr>
                            <%-- ပြင်ဆင်ပြီး - Database ID (#${comment.id}) အစား စဉ်တိုက် Serial Number ထုတ်ပြခြင်း --%>
                            <td><span class="id-badge">${status.count}</span></td>
                            <td>${comment.username}</td>
                            <td>${comment.sheetTitle}</td>
                            <td><div class="text-clip">${comment.commentText}</div></td>
                            <td>
                                <c:choose>
                                    <c:when test="${comment.status == 0}">
                                        <span class="badge bg-danger">Hidden / Blocked</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-success">Active</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-end">
                                <c:if test="${comment.status != 0}">
                                    <%-- နောက်ကွယ်စနစ်အလုပ်လုပ်ရန် data attributes တွေမှာ ${comment.id} မူရင်းကို ဆက်သုံးထားပါသည် --%>
                                    <button type="button" class="btn btn-sm btn-outline-danger hide-btn" 
                                            data-bs-toggle="modal" data-bs-target="#hideReasonModal"
                                            data-id="${comment.id}" 
                                            data-user-id="${comment.userId}"
                                            data-sheet-title="<c:out value='${comment.sheetTitle}'/>">
                                        <i class="fa-solid fa-ban"></i> Hide
                                    </button>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div class="modal fade" id="hideReasonModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content">
            <form action="manage_comments" method="post">
                <input type="hidden" name="action" value="hide">
                <input type="hidden" name="id" id="hideCommentId">
                <input type="hidden" name="userId" id="hideUserId">
                <input type="hidden" name="sheetTitle" id="hideSheetTitle">
                
                <div class="modal-header">
                    <h6 class="fw-bold m-0">Comment ကို ပိတ်ပင်ခြင်း</h6>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <label class="form-label small fw-bold text-secondary">အကြောင်းပြချက် ရွေးချယ်ရန်</label>
                    <select name="reason" class="form-select form-select-sm" required>
                        <option value="မဆီလျော်သော ဆဲဆိုစကားလုံးများ ပါဝင်ခြင်း">🤬 ရိုင်းစိုင်းဆဲဆိုခြင်း (Profanity)</option>
                        <option value="ကြော်ငြာများနှင့် အမှိုက်စာများ (Spam) ဖြစ်ခြင်း">🔗 ကြော်ငြာလင့်ခ်များ / Spam</option>
                        <option value="အပြုသဘောမဆောင်ဘဲ တိုက်ခိုက်ပြောဆိုခြင်း">⚠️ တိုက်ခိုက်ပြောဆိုခြင်း (Hate Speech)</option>
                        <option value="အခြားအကြောင်းပြချက်ကြောင့်">📝 အခြားအကြောင်းပြချက်</option>
                    </select>
                </div>
                <div class="modal-footer py-2">
                    <button type="button" class="btn btn-light border small" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-danger small">Confirm & Notify</button>
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
// Hide Button နှိပ်လိုက်ရင် Modal ထဲကို Data တွေ လှမ်းထည့်ပေးမယ့် Script
$('.hide-btn').on('click', function() {
    $('#hideCommentId').val($(this).data('id'));
    $('#hideUserId').val($(this).data('user-id'));
    $('#hideSheetTitle').val($(this).data('sheet-title'));
});
</script>
</body>
</html>