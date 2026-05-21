<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Account | CheatSheets</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
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
                radial-gradient(circle at 18% 18%, rgba(16, 185, 129, 0.22), transparent 28%),
                radial-gradient(circle at 84% 12%, rgba(91, 95, 247, 0.26), transparent 30%),
                radial-gradient(circle at 60% 90%, rgba(6, 182, 212, 0.2), transparent 30%),
                linear-gradient(135deg, #f8fbff, #f2f7ff 48%, #f7fffb);
            display: grid;
            place-items: center;
            padding: 24px;
        }

        .auth-shell {
            width: min(980px, 100%);
            display: grid;
            grid-template-columns: 0.95fr 1.05fr;
            background: rgba(255, 255, 255, 0.8);
            border: 1px solid rgba(255, 255, 255, 0.86);
            border-radius: 24px;
            box-shadow: 0 30px 90px rgba(15, 23, 42, 0.16);
            overflow: hidden;
            backdrop-filter: blur(20px);
        }

        .auth-card {
            padding: 44px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        .auth-visual {
            padding: 44px;
            background:
                linear-gradient(135deg, rgba(16, 185, 129, 0.94), rgba(91, 95, 247, 0.9)),
                radial-gradient(circle at top left, rgba(255, 255, 255, 0.35), transparent 34%);
            color: #fff;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            min-height: 580px;
        }

        .brand {
            font-size: 20px;
            font-weight: 800;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .visual-title {
            font-size: clamp(32px, 5vw, 52px);
            line-height: 1;
            font-weight: 800;
            letter-spacing: -0.03em;
            margin-bottom: 18px;
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
            background: linear-gradient(135deg, var(--primary), var(--mint));
            color: #fff;
            width: 100%;
            font-weight: 800;
            box-shadow: 0 16px 30px rgba(16, 185, 129, 0.22);
        }

        .btn-auth:hover {
            color: #fff;
            transform: translateY(-1px);
        }

        .back-link {
            color: var(--muted);
            font-size: 14px;
            text-decoration: none;
            font-weight: 600;
        }

        @media (max-width: 820px) {
            .auth-shell { grid-template-columns: 1fr; }
            .auth-visual { min-height: auto; padding: 32px; }
            .auth-card { padding: 32px; }
        }
    </style>
</head>
<body>
<div class="auth-shell">
    <section class="auth-card">
        <a href="${pageContext.request.contextPath}/home" class="back-link"><i class="fa fa-chevron-left me-1"></i> Back to home</a>
        <h2 class="fw-bold mt-4 mb-2">Create your account</h2>
        <p class="text-muted mb-4">Join the workspace and start collecting useful developer references.</p>

        <% if(session.getAttribute("errorMsg") != null) { %>
            <div class="alert alert-danger py-2 small rounded-3 border-0">
                <i class="fa fa-exclamation-circle me-2"></i>
                <%= session.getAttribute("errorMsg") %>
            </div>
            <% session.removeAttribute("errorMsg"); %>
        <% } %>

        <form action="register" method="POST">
            <div class="input-box">
                <i class="fa-regular fa-user text-muted"></i>
                <input type="text" name="username" placeholder="Full name" required>
            </div>
            <div class="input-box">
                <i class="fa-regular fa-envelope text-muted"></i>
                <input type="email" name="email" placeholder="Email address" required>
            </div>
            <div class="input-box">
                <i class="fa-solid fa-lock text-muted"></i>
                <input type="password" name="password" id="passwordField" placeholder="Password" required>
                <i class="fa-solid fa-eye-slash text-muted" id="togglePassword" style="cursor: pointer;"></i>
            </div>
            <button type="submit" class="btn btn-auth mt-2">Create Account</button>
        </form>

        <p class="mt-4 small text-muted mb-0">
            Already have an account?
            <a href="login.jsp" class="fw-bold text-decoration-none">Log in</a>
        </p>
    </section>

    <section class="auth-visual">
        <div class="brand"><i class="fa-solid fa-layer-group"></i> CheatSheets</div>
        <div>
            <h1 class="visual-title">Build your learning library.</h1>
            <p class="opacity-75 mb-0">Browse cheat sheets, add comments, rate helpful content, and keep everything easy to find.</p>
        </div>
        <div class="small opacity-75">Modern notes for practical developers.</div>
    </section>
</div>

<script>
const togglePassword = document.querySelector('#togglePassword');
const password = document.querySelector('#passwordField');
togglePassword.addEventListener('click', function () {
    const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
    password.setAttribute('type', type);
    this.classList.toggle('fa-eye');
    this.classList.toggle('fa-eye-slash');
});
</script>
</body>
</html>
