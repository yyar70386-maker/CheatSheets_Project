<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="uri" value="${pageContext.request.requestURI}"/>

<nav class="navbar navbar-expand-lg border-bottom sticky-top">
    <div class="container">
        <a class="navbar-brand fw-bold fs-4" href="${ctx}/home">
            <i class="fa-solid fa-layer-group brand-mark me-2"></i>CheatSheets
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav"
                aria-controls="mainNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav nav-main gap-lg-1">
                <li class="nav-item">
                    <a class="nav-link ${uri.contains('/home') && empty param.categoryId && empty param.q ? 'active' : ''}" href="${ctx}/home">Categories</a>
                </li>
                <li class="nav-item">
                    <c:choose>
                        <c:when test="${not empty sessionScope.user}">
                            <a class="nav-link ${uri.contains('my-sheets') ? 'active' : ''}" href="${ctx}/my-sheets">Create</a>
                        </c:when>
                        <c:otherwise>
                            <a class="nav-link" href="${ctx}/login.jsp?redirect=my-sheets">Create</a>
                        </c:otherwise>
                    </c:choose>
                </li>
            </ul>

            <form class="navbar-search mx-lg-3 my-3 my-lg-0 flex-grow-1" action="${ctx}/home" method="get" role="search">
                <div class="input-group">
                    <input type="search" name="q" class="form-control" placeholder="Search cheat sheets..."
                           value="${param.q != null ? param.q : searchKeyword}">
                    <button class="btn btn-purple" type="submit" aria-label="Search">
                        <i class="fa-solid fa-magnifying-glass"></i>
                    </button>
                </div>
            </form>

            <div class="d-flex align-items-center gap-2 ms-lg-auto">
                <button type="button" class="btn btn-outline-custom btn-sm" data-theme-toggle aria-label="Toggle theme">
                    <i class="fa-solid fa-moon"></i>
                </button>

                <c:if test="${not empty sessionScope.user}">
                    <a href="${ctx}/notifications" class="btn btn-outline-custom btn-sm position-relative" title="Notifications">
                        <i class="fa-regular fa-bell"></i>
                        <c:if test="${unreadNotifications != null && unreadNotifications > 0}">
                            <span class="noti-badge">${unreadNotifications}</span>
                        </c:if>
                    </a>
                </c:if>

                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <div class="dropdown">
                            <button class="btn btn-outline-custom dropdown-toggle d-flex align-items-center gap-2" type="button" data-bs-toggle="dropdown">
                                <jsp:include page="user_avatar.jsp"/>
                                <span class="fw-medium d-none d-sm-inline">${sessionScope.user.username}</span>
                                <c:if test="${sessionScope.user.role == 1}">
                                    <span class="badge role-badge role-admin ms-1 d-none d-md-inline">Admin</span>
                                </c:if>
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end shadow border-0 mt-2">
                                <li><a class="dropdown-item" href="${ctx}/profile"><i class="fa fa-user me-2 text-muted"></i>My Profile</a></li>
                                <li><a class="dropdown-item" href="${ctx}/my-sheets"><i class="fa fa-file-lines me-2 text-muted"></i>My cheat sheets</a></li>
                                <c:if test="${sessionScope.user.role == 1}">
                                    <li><a class="dropdown-item" href="${ctx}/admin-dashboard"><i class="fa fa-gauge me-2 text-muted"></i>Admin Dashboard</a></li>
                                </c:if>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item text-danger" href="${ctx}/logout"><i class="fa fa-sign-out me-2"></i>Logout</a></li>
                            </ul>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <a href="${ctx}/login.jsp" class="btn btn-link text-decoration-none fw-medium nav-auth-link">Login</a>
                        <a href="${ctx}/register.jsp" class="btn btn-purple btn-sm shadow-sm">Register</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>
