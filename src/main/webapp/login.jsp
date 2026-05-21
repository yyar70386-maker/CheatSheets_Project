<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="fragments/theme_init.jsp"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login | CheatSheets</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="theme.css">
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

        :root {
            --ink: #0f172a;
            --muted: #64748b;
            --primary: #5b5ff7;
            --cyan: #06b6d4;
            --mint: #10b981;
            --line: rgba(226, 232, 240, 0.9);
        }

        body {
            min-height: 100vh;
            margin: 0;
            font-family: 'Inter', sans-serif;
            color: var(--ink);
            background:
                radial-gradient(circle at 15% 15%, rgba(91, 95, 247, 0.28), transparent 30%),
                radial-gradient(circle at 85% 18%, rgba(6, 182, 212, 0.24), transparent 30%),
                radial-gradient(circle at 70% 90%, rgba(16, 185, 129, 0.18), transparent 28%),
                linear-gradient(135deg, #f8fbff, #eef4ff 48%, #f7fffb);
            display: grid;
            place-items: center;
            padding: 24px;
        }

        .auth-shell {
            width: min(980px, 100%);
            display: grid;
            grid-template-columns: 1.1fr 0.9fr;
            background: rgba(255, 255, 255, 0.78);
            border: 1px solid rgba(255, 255, 255, 0.85);
            border-radius: 24px;
            box-shadow: 0 30px 90px rgba(15, 23, 42, 0.16);
            overflow: hidden;
            backdrop-filter: blur(20px);
        }

        .auth-visual {
            padding: 44px;
            background:
                linear-gradient(135deg, rgba(91, 95, 247, 0.94), rgba(6, 182, 212, 0.88)),
                radial-gradient(circle at top right, rgba(255, 255, 255, 0.35), transparent 32%);
            color: #fff;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            min-height: 560px;
        }

        .brand {
            font-size: 20px;
            font-weight: 800;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .visual-title {
            font-size: clamp(32px, 5vw, 54px);
            line-height: 1;
            font-weight: 800;
            letter-spacing: -0.03em;
            margin: 0 0 18px;
        }

        .visual-copy {
            color: rgba(255, 255, 255, 0.82);
            max-width: 440px;
            font-size: 16px;
        }

        .auth-card {
            padding: 44px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        .back-link {
            color: var(--muted);
            font-size: 14px;
            text-decoration: none;
            font-weight: 600;
        }

        .auth-title {
            font-weight: 800;
            letter-spacing: -0.02em;
            margin-top: 28px;
        }

        .input-box {
            height: 52px;
            border: 1px solid var(--line);
            background: #fff;
            border-radius: 12px;
            padding: 0 14px;
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 14px;
        }

        .input-box:focus-within {
            border-color: var(--primary);
            box-shadow: 0 0 0 4px rgba(91, 95, 247, 0.12);
        }

        .input-box input {
            border: 0;
            outline: 0;
            width: 100%;
            background: transparent;
        }

        .btn-auth {
            height: 52px;
            border: 0;
            border-radius: 12px;
            background: linear-gradient(135deg, var(--primary), var(--cyan));
            color: #fff;
            width: 100%;
            font-weight: 800;
            box-shadow: 0 16px 30px rgba(91, 95, 247, 0.24);
        }

        .btn-auth:hover {
            color: #fff;
            transform: translateY(-1px);
        }

        @media (max-width: 820px) {
            .auth-shell { grid-template-columns: 1fr; }
            .auth-visual { min-height: auto; padding: 32px; }
            .auth-card { padding: 32px; }
        }

        [data-theme="dark"] body {
            background: linear-gradient(180deg, #0b1220, #111827);
            color: #e2e8f0;
        }

        [data-theme="dark"] .auth-shell {
            background: rgba(30, 41, 59, 0.92);
            border-color: #334155;
        }

        [data-theme="dark"] .auth-card { color: #e2e8f0; }
        [data-theme="dark"] .input-box { background: #0f172a; border-color: #334155; }
        [data-theme="dark"] .input-box input { color: #e2e8f0; }
        [data-theme="dark"] .back-link { color: #94a3b8; }
    </style>
</head>
<body>
<div class="auth-shell">
    <section class="auth-visual">
        <div class="brand"><i class="fa-solid fa-layer-group"></i> CheatSheets</div>
        <div>
            <h1 class="visual-title">Learn faster with focused developer notes.</h1>
            <p class="visual-copy">Save useful references, explore cheat sheets, and keep your learning workflow clean.</p>
        </div>
        <div class="small opacity-75">Curated resources for everyday development.</div>
    </section>

    <section class="auth-card">
        <div class="d-flex justify-content-between align-items-center">
            <a href="${pageContext.request.contextPath}/home" class="back-link"><i class="fa fa-chevron-left me-1"></i> Back to home</a>
            <button type="button" class="btn btn-sm btn-outline-secondary" data-theme-toggle aria-label="Toggle theme"><i class="fa-solid fa-moon"></i></button>
        </div>
        <h2 class="auth-title">Welcome back</h2>
        <p class="text-muted mb-4">Sign in to continue your learning workspace.</p>

        <% if(session.getAttribute("authError") != null) { %>
            <div class="alert alert-danger py-2 px-3 border-0 rounded-3 small mb-3">
                <i class="fa fa-circle-exclamation me-2"></i>
                <%= session.getAttribute("authError") %>
            </div>
            <% session.removeAttribute("authError"); %>
        <% } %>

        <form action="login" method="POST">
            <c:if test="${not empty param.redirect}">
                <input type="hidden" name="redirect" value="${param.redirect}">
            </c:if>
            <div class="input-box">
                <i class="fa-regular fa-envelope text-muted"></i>
                <input type="email" name="email" placeholder="Email address" required>
            </div>
            <div class="input-box">
                <i class="fa-solid fa-lock text-muted"></i>
                <input type="password" id="password" name="password" placeholder="Password" required>
                <i class="fa fa-eye-slash text-muted" id="togglePassword" style="cursor: pointer;"></i>
            </div>
            <div class="d-flex justify-content-between mb-4">
                <label class="small text-muted"><input type="checkbox" class="form-check-input me-1"> Remember me</label>
                <a href="#" class="small text-decoration-none fw-semibold">Forgot password?</a>
            </div>
            <button type="submit" class="btn btn-auth">Log In</button>
        </form>

        <p class="mt-4 small text-muted mb-0">
            Don't have an account?
            <a href="register.jsp" class="fw-bold text-decoration-none">Create account</a>
        </p>
    </section>
</div>

<script src="app.js"></script>
<script>
const togglePassword = document.querySelector('#togglePassword');
const password = document.querySelector('#password');
togglePassword.addEventListener('click', function () {
    const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
    password.setAttribute('type', type);
    this.classList.toggle('fa-eye');
    this.classList.toggle('fa-eye-slash');
});
</script>
</body>
</html>
