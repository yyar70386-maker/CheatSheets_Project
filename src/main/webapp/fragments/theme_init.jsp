<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script>
(function () {
    var stored = localStorage.getItem('cheatsheets-theme');
    if (stored === 'dark' || stored === 'light') {
        document.documentElement.setAttribute('data-theme', stored);
    }
})();
</script>
