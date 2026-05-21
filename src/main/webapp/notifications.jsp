<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en" data-theme="light">
<head>
    <jsp:include page="fragments/theme_init.jsp"/>
    <meta charset="UTF-8">
    <title>Notifications | CheatSheets</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="theme.css">
</head>
<body>

<jsp:include page="fragments/navbar.jsp"/>

<div class="container my-4" style="max-width: 720px;">
    <h1 class="h4 fw-bold mb-4">Notifications</h1>
    <div class="panel">
        <c:forEach var="n" items="${notifications}">
            <div class="border-top py-3 noti-item ${n.read ? '' : 'noti-item-unread'}">
                <div class="d-flex gap-3">
                    <span class="noti-icon text-primary">
                        <c:choose>
                            <c:when test="${n.type == 'follow'}"><i class="fa fa-user-plus"></i></c:when>
                            <c:when test="${n.type == 'sheet_reaction'}"><i class="fa fa-thumbs-up"></i></c:when>
                            <c:when test="${n.type == 'new_sheet'}"><i class="fa fa-file-lines"></i></c:when>
                            <c:otherwise><i class="fa fa-bell"></i></c:otherwise>
                        </c:choose>
                    </span>
                    <div class="flex-grow-1">
                        <p class="mb-1">${n.message}</p>
                        <div class="d-flex flex-wrap align-items-center gap-2">
                            <small class="text-muted">${n.createdAt}</small>
                            <c:if test="${not empty n.linkUrl}">
                                <a href="${ctx}/${n.linkUrl}" class="small">View</a>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
        <c:if test="${empty notifications}">
            <p class="text-muted mb-0">No notifications yet. You will be notified when someone follows you, reacts to your cheat sheets, or when people you follow publish new content.</p>
        </c:if>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="app.js"></script>
</body>
</html>
