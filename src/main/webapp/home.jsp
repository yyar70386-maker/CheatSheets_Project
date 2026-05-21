<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="javax.servlet.DispatcherType" %>
<%
    if (request.getDispatcherType() == DispatcherType.REQUEST
            && "/home.jsp".equals(request.getServletPath())) {
        response.sendRedirect(request.getContextPath() + "/home");
        return;
    }
%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en" data-theme="light">
<head>
    <jsp:include page="fragments/theme_init.jsp"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CheatSheets - Developer Resources</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="theme.css">
</head>
<body>

<jsp:include page="fragments/navbar.jsp"/>

<div class="container mt-4 mb-5">

    <c:if test="${browseStep != 'categories'}">
        <nav aria-label="breadcrumb" class="browse-breadcrumb mb-4">
            <ol class="breadcrumb mb-0">
                <li class="breadcrumb-item"><a href="${ctx}/home">Categories</a></li>
                <c:if test="${not empty category}">
                    <c:choose>
                        <c:when test="${browseStep == 'category'}">
                            <li class="breadcrumb-item active" aria-current="page">${category.name}</li>
                        </c:when>
                        <c:otherwise>
                            <li class="breadcrumb-item"><a href="${ctx}/home?categoryId=${category.id}">${category.name}</a></li>
                        </c:otherwise>
                    </c:choose>
                </c:if>
                <c:if test="${not empty tag}">
                    <li class="breadcrumb-item active" aria-current="page">${tag.name}</li>
                </c:if>
                <c:if test="${browseStep == 'search'}">
                    <li class="breadcrumb-item active" aria-current="page">Search</li>
                </c:if>
            </ol>
        </nav>
    </c:if>

    <c:if test="${browseStep == 'categories' || empty browseStep}">
        <div class="hero-card row align-items-center mx-0 mb-5">
            <div class="col-lg-8">
                <h1 class="display-5 fw-bold mb-3">Find, Save &amp; Master <br><span class="text-primary">Developer</span> Cheatsheets</h1>
                <p class="text-muted fs-5 mb-4">Pick a category to browse cheatsheets, then narrow down by tags.</p>
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <a href="${ctx}/my-sheets" class="btn btn-outline-custom"><i class="fa fa-plus me-2"></i>Create a cheatsheet</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${ctx}/login.jsp?redirect=my-sheets" class="btn btn-outline-custom"><i class="fa fa-plus me-2"></i>Create a cheatsheet</a>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="col-lg-4 d-none d-lg-block text-end">
                <i class="fa-solid fa-folder-tree fa-8x text-primary opacity-25"></i>
            </div>
        </div>

        <div class="browse-step-header mb-4">
            <span class="step-pill active">1</span>
            <h4 class="fw-bold mb-1 d-inline-block ms-2 align-middle">Choose a category</h4>
            <p class="text-muted mb-0 ms-5 ps-2">Select where you want to start browsing.</p>
        </div>

        <div class="row g-4">
            <c:forEach var="cat" items="${categoryList}">
                <div class="col-lg-3 col-md-4 col-6">
                    <a href="${ctx}/home?categoryId=${cat.id}" class="card cat-card browse-card h-100 p-4 text-center text-decoration-none">
                        <div class="icon-wrapper mx-auto mb-3"><i class="fa-solid fa-layer-group fs-4"></i></div>
                        <h6 class="fw-bold mb-1">${cat.name}</h6>
                        <p class="text-muted small mb-2 line-clamp-2">${cat.description}</p>
                        <span class="badge badge-cat">${categorySheetCounts[cat.id]} cheatsheet(s)</span>
                    </a>
                </div>
            </c:forEach>
        </div>
    </c:if>

    <c:if test="${browseStep == 'category'}">
        <div class="browse-step-header mb-4">
            <span class="step-pill active">2</span>
            <h4 class="fw-bold mb-1 d-inline-block ms-2 align-middle">${category.name}</h4>
            <p class="text-muted mb-0 ms-5 ps-2">${category.description}</p>
        </div>

        <c:if test="${not empty tagList}">
            <div class="mb-4">
                <h6 class="fw-bold text-muted text-uppercase small mb-3">Filter by tag (step 3)</h6>
                <div class="tag-chip-row">
                    <a href="${ctx}/home?categoryId=${category.id}" class="tag-chip active">All in category</a>
                    <c:forEach var="t" items="${tagList}">
                        <a href="${ctx}/home?categoryId=${category.id}&amp;tagId=${t.id}" class="tag-chip">${t.name}</a>
                    </c:forEach>
                </div>
            </div>
        </c:if>

        <jsp:include page="fragments/browse_sheet_grid.jsp"/>
    </c:if>

    <c:if test="${browseStep == 'tag'}">
        <div class="browse-step-header mb-4">
            <span class="step-pill active">3</span>
            <h4 class="fw-bold mb-1 d-inline-block ms-2 align-middle">${category.name} &rarr; ${tag.name}</h4>
            <p class="text-muted mb-0 ms-5 ps-2">Cheatsheets in this category with the &ldquo;${tag.name}&rdquo; tag.</p>
        </div>

        <c:if test="${not empty tagList}">
            <div class="mb-4">
                <h6 class="fw-bold text-muted text-uppercase small mb-3">Tags in ${category.name}</h6>
                <div class="tag-chip-row">
                    <a href="${ctx}/home?categoryId=${category.id}" class="tag-chip">All in category</a>
                    <c:forEach var="t" items="${tagList}">
                        <a href="${ctx}/home?categoryId=${category.id}&amp;tagId=${t.id}"
                           class="tag-chip ${t.id == tag.id ? 'active' : ''}">${t.name}</a>
                    </c:forEach>
                </div>
            </div>
        </c:if>

        <jsp:include page="fragments/browse_sheet_grid.jsp"/>
    </c:if>

    <c:if test="${browseStep == 'search'}">
        <div class="browse-step-header mb-4">
            <h4 class="fw-bold mb-1">Search results</h4>
            <p class="text-muted mb-0">Showing matches for &ldquo;${searchKeyword}&rdquo;</p>
        </div>
        <jsp:include page="fragments/browse_sheet_grid.jsp"/>
    </c:if>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="app.js"></script>
</body>
</html>
