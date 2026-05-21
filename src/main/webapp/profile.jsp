<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en" data-theme="light">
<head>
    <jsp:include page="fragments/theme_init.jsp"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile | CheatSheets</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="theme.css">
</head>
<body>

<jsp:include page="fragments/navbar.jsp"/>

<div class="container my-4">
    <div class="panel mb-4">
        <div class="row g-4 align-items-center">
            <div class="col-md-3 text-center">
                <% request.setAttribute("avatarUser", request.getAttribute("profileUser")); %>
                <jsp:include page="fragments/user_avatar.jsp">
                    <jsp:param name="size" value="avatar-lg mb-3"/>
                </jsp:include>
                <form action="profile" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="avatar">
                    <input type="file" name="avatar" accept="image/*" class="form-control form-control-sm mb-2" required>
                    <button class="btn btn-outline-custom btn-sm w-100">Update photo</button>
                </form>
            </div>
            <div class="col-md-9">
                <form action="profile" method="post">
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Username</label>
                        <input type="text" name="username" class="form-control" value="${profileUser.username}" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Bio</label>
                        <textarea name="bio" class="form-control" rows="3" placeholder="Tell others about you...">${profileUser.bio}</textarea>
                    </div>
                    <p class="text-muted small mb-3">${profileUser.email}</p>
                    <div class="d-flex gap-4 mb-3">
                        <a href="connections?userId=${profileUser.id}&tab=followers" class="text-decoration-none">
                            <strong>${followerCount}</strong> <span class="text-muted">Followers</span>
                        </a>
                        <a href="connections?userId=${profileUser.id}&tab=following" class="text-decoration-none">
                            <strong>${followingCount}</strong> <span class="text-muted">Following</span>
                        </a>
                    </div>
                    <button class="btn btn-purple">Save profile</button>
                </form>
            </div>
        </div>
    </div>

    <div class="row g-4">
        <div class="col-lg-6">
            <div class="panel">
                <h5 class="fw-bold mb-3">My cheat sheets</h5>
                <c:forEach var="s" items="${userSheets}">
                    <div class="d-flex justify-content-between border-top py-2">
                        <%-- ပြင်ဆင်ပြီး - Link အမှန်ရရှိရန် ${s.id} သို့ ပြောင်းလဲထားသည် --%>
                        <a href="sheet?id=${s.id}" class="text-decoration-none fw-semibold">${s.title}</a>
                        <a href="my-sheets" class="small">Manage</a>
                    </div>
                </c:forEach>
                <c:if test="${empty userSheets}"><p class="text-muted small mb-0">No posts yet. <a href="my-sheets">Create one</a>.</p></c:if>
            </div>
        </div>
        <div class="col-lg-6">
            <div class="panel mb-4">
                <h5 class="fw-bold mb-3">Saved bookmarks</h5>
                <c:forEach var="s" items="${savedSheets}">
                    <%-- ပြင်ဆင်ပြီး - Link အမှန်ရရှိရန် ${s.id} သို့ ပြောင်းလဲထားသည် --%>
                    <div class="border-top py-2"><a href="sheet?id=${s.id}" class="text-decoration-none">${s.title}</a></div>
                </c:forEach>
                <c:if test="${empty savedSheets}"><p class="text-muted small mb-0">No saved sheets.</p></c:if>
            </div>
            <div class="panel mb-4">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5 class="fw-bold mb-0">Followers</h5>
                    <a href="connections?userId=${profileUser.id}&tab=followers" class="small">View all</a>
                </div>
                <c:forEach var="u" items="${followersList}">
                    <div class="d-flex align-items-center gap-2 border-top py-2">
                        <% request.setAttribute("avatarUser", pageContext.getAttribute("u")); %>
                        <jsp:include page="fragments/user_avatar.jsp"/>
                        <a href="user?id=${u.id}" class="text-decoration-none fw-semibold">${u.username}</a>
                    </div>
                </c:forEach>
                <c:if test="${empty followersList}">
                    <p class="text-muted small mb-0">No followers yet.</p>
                </c:if>
            </div>
            <div class="panel mt-4">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5 class="fw-bold mb-0">Following</h5>
                    <a href="connections?userId=${profileUser.id}&tab=following" class="small">View all</a>
                </div>
                <c:forEach var="u" items="${followingList}">
                    <div class="d-flex align-items-center gap-2 border-top py-2">
                        <% request.setAttribute("avatarUser", pageContext.getAttribute("u")); %>
                        <jsp:include page="fragments/user_avatar.jsp"/>
                        <a href="user?id=${u.id}" class="text-decoration-none fw-semibold">${u.username}</a>
                    </div>
                </c:forEach>
                <c:if test="${empty followingList}">
                    <p class="text-muted small mb-0">Not following anyone yet.</p>
                </c:if>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="app.js"></script>
</body>
</html>