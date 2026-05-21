<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String ctx = request.getContextPath();
%>
<script>
window.CHEAT_SHEET_CTX = '<%= ctx %>';
window.CHEAT_SHEET_UPLOAD_URL = '<%= ctx %>/sheet-image-upload';
</script>
