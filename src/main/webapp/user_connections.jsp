<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en" data-theme="light">
<head>
    <jsp:include page="fragments/theme_init.jsp"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${profileUser.username} — ${activeTab == 'following' ? 'Following' : 'Followers'} | CheatSheets</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="theme.css">
</head>
<body>

<jsp:include page="fragments/navbar.jsp"/>

<div class="container my-4" style="max-width: 640px;">
    <div class="mb-3">
        <a href="${ownProfile ? 'profile' : 'user?id='.concat(profileUser.id)}" class="text-decoration-none small text-muted">
            <i class="fa fa-arrow-left me-1"></i>Back to profile
        </a>
    </div>

    <div class="panel mb-4">
        <div class="d-flex align-items-center gap-3 mb-3">
            <% request.setAttribute("avatarUser", request.getAttribute("profileUser")); %>
            <jsp:include page="fragments/user_avatar.jsp"/>
            <div>
                <h1 class="h5 fw-bold mb-0">${profileUser.username}</h1>
                <p class="text-muted small mb-0">${followerCount} followers · ${followingCount} following</p>
            </div>
        </div>
        <ul class="nav nav-pills connection-tabs gap-2">
            <li class="nav-item">
                <a class="nav-link ${activeTab == 'followers' ? 'active' : ''}"
                   href="${ctx}/connections?userId=${profileUser.id}&tab=followers">Followers</a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${activeTab == 'following' ? 'active' : ''}"
                   href="${ctx}/connections?userId=${profileUser.id}&tab=following">Following</a>
            </li>
        </ul>
    </div>

    <div class="panel">
        <c:forEach var="u" items="${connectionList}">
            <div class="d-flex align-items-center justify-content-between gap-3 border-top py-3 connection-row">
                <div class="d-flex align-items-center gap-3 min-w-0">
                    <% request.setAttribute("avatarUser", pageContext.getAttribute("u")); %>
                    <jsp:include page="fragments/user_avatar.jsp">
                        <jsp:param name="size" value="flex-shrink-0"/>
                    </jsp:include>
                    <div class="min-w-0">
                        <a href="${ctx}/user?id=${u.id}" class="fw-semibold text-decoration-none text-truncate d-block">${u.username}</a>
                        <c:if test="${not empty u.bio}">
                            <p class="text-muted small mb-0 text-truncate">${u.bio}</p>
                        </c:if>
                    </div>
                </div>
                <c:if test="${ownProfile && activeTab == 'following'}">
                    <form action="${ctx}/social" method="post" class="flex-shrink-0">
                        <input type="hidden" name="action" value="unfollow">
                        <input type="hidden" name="userId" value="${u.id}">
                        <input type="hidden" name="redirect" value="connections?userId=${profileUser.id}&tab=following">
                        <button type="submit" class="btn btn-outline-custom btn-sm">Unfollow</button>
                    </form>
                </c:if>
            </div>
        </c:forEach>
        <c:if test="${empty connectionList}">
            <p class="text-muted mb-0 py-2">
                <c:choose>
                    <c:when test="${activeTab == 'following'}">Not following anyone yet.</c:when>
                    <c:otherwise>No followers yet.</c:otherwise>
                </c:choose>
            </p>
        </c:if>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="app.js"></script>
</body>
</html>
