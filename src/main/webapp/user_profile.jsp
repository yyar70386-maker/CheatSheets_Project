<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.library.util.StarUtil" %>
<!DOCTYPE html>
<html lang="en" data-theme="light">
<head>
    <jsp:include page="fragments/theme_init.jsp"/>
    <meta charset="UTF-8">
    <title>${profileUser.username} | CheatSheets</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="theme.css">
</head>
<body>

<jsp:include page="fragments/navbar.jsp"/>

<div class="container my-4">
    <div class="panel mb-4">
        <div class="d-flex flex-wrap align-items-center gap-4">
            <% request.setAttribute("avatarUser", request.getAttribute("profileUser")); %>
            <jsp:include page="fragments/user_avatar.jsp">
                <jsp:param name="size" value="avatar-lg"/>
            </jsp:include>
            <div class="flex-grow-1">
                <h2 class="fw-bold mb-1">${profileUser.username}</h2>
                <p class="text-muted mb-2">${empty profileUser.bio ? 'No bio yet.' : profileUser.bio}</p>
                <div class="d-flex gap-4 small mb-3">
                    <a href="connections?userId=${profileUser.id}&tab=followers" class="text-decoration-none">
                        <strong>${followerCount}</strong> followers
                    </a>
                    <a href="connections?userId=${profileUser.id}&tab=following" class="text-decoration-none">
                        <strong>${followingCount}</strong> following
                    </a>
                </div>
                <c:if test="${viewerLoggedIn && not ownProfile}">
                    <form action="social" method="post" class="d-inline">
                        <input type="hidden" name="action" value="${isFollowing ? 'unfollow' : 'follow'}">
                        <input type="hidden" name="userId" value="${profileUser.id}">
                        <input type="hidden" name="redirect" value="user?id=${profileUser.id}">
                        <button type="submit" class="btn ${isFollowing ? 'btn-outline-custom' : 'btn-purple'}">
                            ${isFollowing ? 'Unfollow' : 'Follow'}
                        </button>
                    </form>
                </c:if>
                <c:if test="${not viewerLoggedIn}">
                    <a href="login.jsp?redirect=user?id=${profileUser.id}" class="btn btn-purple btn-sm">Log in to follow</a>
                </c:if>
            </div>
        </div>
    </div>

    <div class="panel">
        <h5 class="fw-bold mb-3">${profileUser.username}'s cheat sheets</h5>
        <c:forEach var="s" items="${userSheets}">
            <div class="d-flex justify-content-between align-items-center border-top py-3">
                <div>
                    <a href="sheet?id=${s.id}" class="fw-semibold text-decoration-none">${s.title}</a>
                    <div class="small text-muted">${s.categoryName}</div>
                </div>
                <span class="star-rating"><%= StarUtil.renderStars(((com.library.model.Cheatsheets) pageContext.getAttribute("s")).getAverageRating()) %></span>
            </div>
        </c:forEach>
        <c:if test="${empty userSheets}">
            <p class="text-muted mb-0">No public cheat sheets yet.</p>
        </c:if>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="app.js"></script>
</body>
</html>
