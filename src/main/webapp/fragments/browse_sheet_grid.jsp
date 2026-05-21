<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.library.util.StarUtil" %>
<%@ page import="com.library.util.ContentUtil" %>
<%@ page import="com.library.model.Cheatsheets" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<c:choose>
    <c:when test="${empty sheetList}">
        <div class="empty-state text-center py-5">
            <i class="fa-solid fa-file-circle-question fa-3x text-muted mb-3"></i>
            <p class="text-muted mb-3">No cheatsheets found at this step.</p>
            <a href="${ctx}/home" class="btn btn-outline-custom">Back to categories</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="row g-4">
            <c:forEach var="sheet" items="${sheetList}">
                <div class="col-md-6 col-lg-4">
                    <div class="card sheet-card h-100 p-3">
                        <h5 class="fw-bold mb-2">${sheet.title}</h5>
                        <p class="text-muted small sheet-preview mb-3"><%= ContentUtil.toPreview(((Cheatsheets) pageContext.getAttribute("sheet")).getContent(), 140) %></p>
                        <div class="d-flex align-items-center justify-content-between mt-auto pt-2 border-top">
                            <span class="badge badge-cat">${sheet.categoryName}</span>
                            <span class="star-rating"><%= StarUtil.renderStars(((com.library.model.Cheatsheets) pageContext.getAttribute("sheet")).getAverageRating()) %></span>
                        </div>
                        <div class="d-flex align-items-center justify-content-between mt-2 small text-muted">
                            <span>by ${sheet.authorName}</span>
                            <span><i class="fa fa-download me-1"></i>${sheet.downloadCount}</span>
                        </div>
                        <a href="${ctx}/sheet?id=${sheet.id}" class="btn btn-outline-custom mt-3 w-100">View details</a>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>
