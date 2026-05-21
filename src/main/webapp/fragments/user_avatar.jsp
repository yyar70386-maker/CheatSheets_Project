<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.library.model.Users" %>
<%@ page import="com.library.util.AvatarUtil" %>
<%
    Users avatarUser = (Users) request.getAttribute("avatarUser");
    if (avatarUser == null) {
        avatarUser = (Users) session.getAttribute("user");
    }
    String sizeClass = request.getParameter("size");
    if (sizeClass == null || sizeClass.isBlank()) {
        sizeClass = "";
    }
    String ctx = request.getContextPath();
    boolean custom = AvatarUtil.hasCustomAvatar(avatarUser);
    String imgUrl = custom ? AvatarUtil.resolveCustomUrl(ctx, avatarUser) : null;
    pageContext.setAttribute("avatarCustom", custom);
    pageContext.setAttribute("avatarImgUrl", imgUrl);
    pageContext.setAttribute("avatarSizeClass", sizeClass);
%>

<c:choose>
    <c:when test="${avatarCustom}">
        <img src="${avatarImgUrl}" alt="" class="avatar ${avatarSizeClass}" loading="lazy">
    </c:when>
    <c:otherwise>
        <span class="avatar avatar-default ${avatarSizeClass}" aria-hidden="true">
            <i class="fa-solid fa-user"></i>
        </span>
    </c:otherwise>
</c:choose>
