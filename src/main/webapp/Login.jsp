<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MasterAnnonce - Connexion</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        :root {
            --primary: #2563eb;
            --primary-hover: #1d4ed8;
            --bg: #f8fafc;
            --text-main: #1e293b;
            --text-muted: #64748b;
            --error: #ef4444;
        }
        body {
            font-family: 'Inter', sans-serif;
            background-color: var(--bg);
            color: var(--text-main);
            margin: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            padding: 20px;
        }
        .login-card {
            background: white;
            padding: 40px;
            border-radius: 16px;
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 400px;
            border: 1px solid #e2e8f0;
        }
        .header {
            text-align: center;
            margin-bottom: 32px;
        }
        h1 {
            font-size: 1.5rem;
            font-weight: 700;
            margin: 0 0 8px 0;
            color: var(--text-main);
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            font-size: 0.875rem;
        }
        input {
            width: 100%;
            padding: 12px;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            font-size: 0.95rem;
            box-sizing: border-box;
            transition: all 0.2s;
        }
        input:focus {
            outline: none;
            border-color: var(--primary);
            box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
        }
        .btn-login {
            width: 100%;
            padding: 12px;
            background: var(--primary);
            color: white;
            border: none;
            border-radius: 8px;
            font-weight: 600;
            font-size: 1rem;
            cursor: pointer;
            transition: background 0.2s;
        }
        .btn-login:hover {
            background: var(--primary-hover);
        }
        .error-message {
            background: #fef2f2;
            color: var(--error);
            padding: 12px;
            border-radius: 8px;
            border: 1px solid #fee2e2;
            margin-bottom: 20px;
            font-size: 0.875rem;
            text-align: center;
        }
        .footer-links {
            margin-top: 24px;
            text-align: center;
            font-size: 0.875rem;
            color: var(--text-muted);
        }
        .footer-links a {
            color: var(--primary);
            text-decoration: none;
            font-weight: 600;
        }
        .footer-links a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>

<div class="login-card">
    <div class="header">
        <h1>Connexion</h1>
        <p style="color: var(--text-muted)">Accédez à votre espace MasterAnnonce</p>
    </div>

    <c:if test="${not empty error}">
        <div class="error-message">
            <i class="fas fa-exclamation-circle"></i> ${error}
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <div class="form-group">
            <label for="username">Nom d'utilisateur</label>
            <input type="text" id="username" name="username" required
                   placeholder="Entrez votre pseudo" value="${param.username}">
        </div>

        <button type="submit" class="btn-login">Se connecter</button>
    </form>

    <div class="footer-links">
        Pas encore de compte ?
        <a href="${pageContext.request.contextPath}/register">Créer un compte</a>
    </div>
</div>

</body>
</html>