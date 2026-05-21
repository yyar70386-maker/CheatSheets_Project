<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.library.util.StarUtil" %>
<%@ page import="com.library.util.AvatarUtil" %>
<!DOCTYPE html>
<html lang="en" data-theme="light">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${sheet.title} | CheatSheets</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="theme.css">
    <style>
        .rating-stars-input input { display: none; }
        .rating-stars-input label { cursor: pointer; font-size: 1.4rem; color: #cbd5e1; }
        .rating-stars-input input:checked ~ label,
        .rating-stars-input label:hover,
        .rating-stars-input label:hover ~ label { color: #f59e0b; }
        .comment-item { border-top: 1px solid var(--line); padding: 16px 0; }
        .reply-item { margin-left: 28px; border-left: 2px solid var(--line); padding: 12px 0 12px 16px; }
        .reply-form, .edit-form { display: none; margin-top: 8px; }
        .reaction-bar .btn.active { background: #eef2ff; border-color: #6366f1; color: #6366f1; }
    </style>
</head>
<body>

<jsp:include page="fragments/navbar.jsp"/>

<main class="container my-4">
    <div class="panel mb-4">
        <div class="d-flex justify-content-between flex-wrap gap-3 mb-3">
            <div>
                <h2 class="fw-bold mb-2">${sheet.title}</h2>
                <div class="text-muted small d-flex flex-wrap align-items-center gap-3">
                    <span><i class="fa fa-layer-group me-1"></i>${sheet.categoryName}</span>
                    <a href="user?id=${sheet.authorId}" class="text-decoration-none">
                        <i class="fa fa-user me-1"></i>${empty sheet.authorName ? 'Unknown' : sheet.authorName}
                    </a>
                    <span class="star-rating"><%= StarUtil.renderStars(((com.library.model.Cheatsheets) request.getAttribute("sheet")).getAverageRating()) %></span>
                </div>
            </div>
            <div class="d-flex flex-wrap gap-2 align-self-start">
                <c:if test="${not empty sessionScope.user}">
                    <form action="social" method="post" class="d-inline">
                        <input type="hidden" name="action" value="${saved ? 'unfavorite' : 'favorite'}">
                        <input type="hidden" name="sheetId" value="${sheet.id}">
                        <input type="hidden" name="redirect" value="sheet?id=${sheet.id}">
                        <button type="submit" class="btn btn-outline-custom btn-sm">
                            <i class="fa-${saved ? 'solid' : 'regular'} fa-bookmark me-1"></i>${saved ? 'Saved' : 'Save'}
                        </button>
                    </form>
                </c:if>
                <a href="home" class="btn btn-light border btn-sm">Back</a>
            </div>
        </div>

        <div class="content-box rich-content mb-3">${sheet.content}</div>

        <c:if test="${not empty sessionScope.user}">
            <div class="reaction-bar d-flex flex-wrap gap-2 align-items-center border-top pt-3">
                <span class="small text-muted me-2">React:</span>
                <form action="social" method="post" class="d-inline">
                    <input type="hidden" name="action" value="sheetReaction">
                    <input type="hidden" name="sheetId" value="${sheet.id}">
                    <input type="hidden" name="reaction" value="like">
                    <input type="hidden" name="redirect" value="sheet?id=${sheet.id}">
                    <button type="submit" class="btn btn-sm btn-outline-custom ${userSheetReaction == 'like' ? 'active' : ''}"><i class="fa-regular fa-thumbs-up"></i></button>
                </form>
                <form action="social" method="post" class="d-inline">
                    <input type="hidden" name="action" value="sheetReaction">
                    <input type="hidden" name="sheetId" value="${sheet.id}">
                    <input type="hidden" name="reaction" value="love">
                    <input type="hidden" name="redirect" value="sheet?id=${sheet.id}">
                    <button type="submit" class="btn btn-sm btn-outline-custom ${userSheetReaction == 'love' ? 'active' : ''}"><i class="fa-regular fa-heart"></i></button>
                </form>
                <form action="social" method="post" class="d-inline">
                    <input type="hidden" name="action" value="sheetReaction">
                    <input type="hidden" name="sheetId" value="${sheet.id}">
                    <input type="hidden" name="reaction" value="insightful">
                    <input type="hidden" name="redirect" value="sheet?id=${sheet.id}">
                    <button type="submit" class="btn btn-sm btn-outline-custom ${userSheetReaction == 'insightful' ? 'active' : ''}"><i class="fa-regular fa-lightbulb"></i></button>
                </form>
                <span class="small text-muted ms-2">${sheetReactionCount} reactions</span>
            </div>
        </c:if>
        <c:if test="${empty sessionScope.user}">
            <p class="small text-muted mb-0 border-top pt-3"><a href="login.jsp?redirect=sheet?id=${sheet.id}">Log in</a> to save, rate, comment, or react.</p>
        </c:if>
    </div>

    <div class="row g-4">
        <div class="col-lg-5">
            <div class="panel">
                <h5 class="fw-bold mb-3">Rate this sheet</h5>
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <form action="sheet" method="post">
                            <input type="hidden" name="action" value="rating">
                            <input type="hidden" name="sheetId" value="${sheet.id}">
                            <div class="rating-stars-input d-flex flex-row-reverse justify-content-end gap-1 mb-3">
                                <c:forEach begin="1" end="5" step="1" var="s">
                                    <input type="radio" name="rating" id="star${s}" value="${6 - s}" required>
                                    <label for="star${6 - s}"><i class="fa-solid fa-star"></i></label>
                                </c:forEach>
                            </div>
                            <button class="btn btn-purple btn-sm">Submit rating</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <span class="star-rating"><%= StarUtil.renderStars(((com.library.model.Cheatsheets) request.getAttribute("sheet")).getAverageRating()) %></span>
                    </c:otherwise>
                </c:choose>
                <h6 class="fw-bold mt-4 mb-2">Recent ratings</h6>
                <c:forEach var="rating" items="${ratingList}">
                    <div class="d-flex justify-content-between border-top py-2">
                        <span>${rating.username}</span>
                        <span class="star-rating"><%= StarUtil.renderStars(((com.library.model.Ratings) pageContext.getAttribute("rating")).getRating()) %></span>
                    </div>
                </c:forEach>
            </div>
        </div>

        <div class="col-lg-7">
            <div class="panel">
                <h5 class="fw-bold mb-3">Comments</h5>
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <form action="sheet" method="post" class="mb-4">
                            <input type="hidden" name="action" value="comment">
                            <input type="hidden" name="sheetId" value="${sheet.id}">
                            <input type="hidden" name="parentId" value="">
                            <textarea name="commentText" class="form-control mb-2" rows="3" placeholder="Write a comment..." required></textarea>
                            <button class="btn btn-purple btn-sm">Post comment</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted"><a href="login.jsp?redirect=sheet?id=${sheet.id}">Log in</a> to comment.</p>
                    </c:otherwise>
                </c:choose>

                <c:forEach var="comment" items="${commentList}">
                    <c:if test="${empty comment.parentId}">
                        <div class="comment-item" id="comment-${comment.id}">
                            <div class="d-flex gap-2">
                                <img src="<%= AvatarUtil.resolveUrl(request.getContextPath(), (String) null) %>" alt="" class="avatar d-none">
                                <div class="flex-grow-1">
                                    <div class="fw-semibold">${comment.username}</div>
                                    <div id="text-${comment.id}">${comment.commentText}</div>
                                    <c:if test="${not empty sessionScope.user}">
                                        <form action="sheet" method="post" class="edit-form" id="editForm${comment.id}">
                                            <input type="hidden" name="action" value="editComment">
                                            <input type="hidden" name="sheetId" value="${sheet.id}">
                                            <input type="hidden" name="commentId" value="${comment.id}">
                                            <textarea name="commentText" class="form-control form-control-sm mb-2" rows="2" required>${comment.commentText}</textarea>
                                            <button class="btn btn-sm btn-primary">Save</button>
                                            <button type="button" class="btn btn-sm btn-light" onclick="hideEdit(${comment.id})">Cancel</button>
                                        </form>
                                    </c:if>
                                    <div class="d-flex flex-wrap gap-2 align-items-center mt-2">
                                        <small class="text-muted">${comment.createdAt}</small>
                                        <c:if test="${not empty sessionScope.user}">
                                            <button type="button" class="btn btn-link btn-sm p-0" onclick="toggleReplyForm(${comment.id})">Reply</button>
                                            <c:if test="${sessionScope.user.id == comment.userId}">
                                                <button type="button" class="btn btn-link btn-sm p-0" onclick="showEdit(${comment.id})">Edit</button>
                                            </c:if>
                                            <form action="social" method="post" class="d-inline">
                                                <input type="hidden" name="action" value="commentReaction">
                                                <input type="hidden" name="commentId" value="${comment.id}">
                                                <input type="hidden" name="reaction" value="like">
                                                <input type="hidden" name="redirect" value="sheet?id=${sheet.id}">
                                                <button type="submit" class="btn btn-link btn-sm p-0 ${comment.userReaction == 'like' ? 'text-primary' : ''}">
                                                    <i class="fa-regular fa-thumbs-up"></i> ${comment.likeCount}
                                                </button>
                                            </form>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <c:forEach var="reply" items="${commentList}">
                            <c:if test="${reply.parentId == comment.id}">
                                <div class="reply-item">
                                    <div class="fw-semibold">${reply.username}</div>
                                    <div>
                                        <c:if test="${not empty reply.parentUsername}">
                                            <span class="mention-tag">@${reply.parentUsername}</span>
                                        </c:if>
                                        ${reply.commentText}
                                    </div>
                                    <div class="d-flex flex-wrap gap-2 mt-1">
                                        <small class="text-muted">${reply.createdAt}</small>
                                        <c:if test="${not empty sessionScope.user && sessionScope.user.id == reply.userId}">
                                            <button type="button" class="btn btn-link btn-sm p-0" onclick="showEdit(${reply.id})">Edit</button>
                                        </c:if>
                                        <c:if test="${not empty sessionScope.user}">
                                            <form action="social" method="post" class="d-inline">
                                                <input type="hidden" name="action" value="commentReaction">
                                                <input type="hidden" name="commentId" value="${reply.id}">
                                                <input type="hidden" name="reaction" value="like">
                                                <input type="hidden" name="redirect" value="sheet?id=${sheet.id}">
                                                <button type="submit" class="btn btn-link btn-sm p-0">
                                                    <i class="fa-regular fa-thumbs-up"></i> ${reply.likeCount}
                                                </button>
                                            </form>
                                        </c:if>
                                    </div>
                                    <c:if test="${not empty sessionScope.user && sessionScope.user.id == reply.userId}">
                                        <form action="sheet" method="post" class="edit-form" id="editForm${reply.id}">
                                            <input type="hidden" name="action" value="editComment">
                                            <input type="hidden" name="sheetId" value="${sheet.id}">
                                            <input type="hidden" name="commentId" value="${reply.id}">
                                            <textarea name="commentText" class="form-control form-control-sm mb-2" rows="2" required>${reply.commentText}</textarea>
                                            <button class="btn btn-sm btn-primary">Save</button>
                                        </form>
                                    </c:if>
                                </div>
                            </c:if>
                        </c:forEach>

                        <c:if test="${not empty sessionScope.user}">
                            <form action="sheet" method="post" class="reply-form mb-3" id="replyForm${comment.id}">
                                <input type="hidden" name="action" value="comment">
                                <input type="hidden" name="sheetId" value="${sheet.id}">
                                <input type="hidden" name="parentId" value="${comment.id}">
                                <textarea name="commentText" class="form-control mb-2" rows="2" placeholder="Reply to @${comment.username}..." required></textarea>
                                <button class="btn btn-sm btn-purple">Post reply</button>
                            </form>
                        </c:if>
                    </c:if>
                </c:forEach>
            </div>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="app.js"></script>
<script>
function toggleReplyForm(id) {
    var f = document.getElementById('replyForm' + id);
    if (f) f.style.display = f.style.display === 'block' ? 'none' : 'block';
}
function showEdit(id) {
    document.getElementById('text-' + id).style.display = 'none';
    document.getElementById('editForm' + id).style.display = 'block';
}
function hideEdit(id) {
    document.getElementById('text-' + id).style.display = 'block';
    document.getElementById('editForm' + id).style.display = 'none';
}
</script>
</body>
</html>
