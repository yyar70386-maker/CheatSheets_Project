<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    String categorySelectId = (String) request.getAttribute("categorySelectId");
    String customWrapId = (String) request.getAttribute("customWrapId");
    String customInputId = (String) request.getAttribute("customInputId");
    if (categorySelectId == null) categorySelectId = "categoryId";
    if (customWrapId == null) customWrapId = "customCategoryWrap";
    if (customInputId == null) customInputId = "customCategoryName";
%>
<label class="form-label small fw-bold">Category</label>
<select name="categoryId" id="<%= categorySelectId %>" class="form-select bg-light border-0 sheet-category-select" required>
    <option value="">Select category</option>
    <c:forEach var="cat" items="${categoryList}">
        <option value="${cat.id}">${cat.name}</option>
    </c:forEach>
    <option value="other">Other (type your own)</option>
</select>
<div id="<%= customWrapId %>" class="custom-category-wrap mt-2 d-none">
    <label class="form-label small fw-bold mb-1" for="<%= customInputId %>">Custom category name</label>
    <input type="text" name="customCategoryName" id="<%= customInputId %>"
           class="form-control bg-light border-0" maxlength="100"
           placeholder="e.g. Mobile Development">
    <div class="form-text">A new category will be created if it does not already exist.</div>
</div>
