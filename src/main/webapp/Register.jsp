<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>MasterAnnonce - Inscription</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        :root {
            --primary: #2563eb;
            --primary-hover: #1d4ed8;
            --bg: #f8fafc;
            --error-bg: #fef2f2;
            --error-text: #b91c1c;
            --error-border: #fecaca;
        }
        body { font-family: 'Inter', sans-serif; background: var(--bg); display: flex; align-items: center; justify-content: center; min-height: 100vh; margin: 0; }
        .register-card { background: white; padding: 40px; border-radius: 16px; box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1); width: 100%; max-width: 400px; border: 1px solid #e2e8f0; }
        h1 { font-size: 1.5rem; margin-bottom: 8px; text-align: center; color: #1e293b; font-weight: 700; }
        p.subtitle { text-align: center; color: #64748b; margin-bottom: 24px; font-size: 0.875rem; }

        /* Alert Box Style */
        .error-alert {
            background: var(--error-bg);
            color: var(--error-text);
            padding: 12px 16px;
            border-radius: 8px;
            border: 1px solid var(--error-border);
            margin-bottom: 24px;
            font-size: 0.875rem;
            display: flex;
            align-items: flex-start;
            gap: 10px;
        }
        .error-alert i { margin-top: 2px; }

        .form-group { margin-bottom: 16px; }
        label { display: block; margin-bottom: 6px; font-size: 0.875rem; font-weight: 600; color: #334155; }
        input {
            width: 100%; padding: 12px; border: 1px solid #e2e8f0; border-radius: 8px; box-sizing: border-box;
            transition: all 0.2s; font-size: 1rem;
        }
        input:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1); }
        input.has-error { border-color: var(--error-text); background-color: var(--error-bg); }

        button { width: 100%; padding: 12px; background: var(--primary); color: white; border: none; border-radius: 8px; font-weight: 600; cursor: pointer; transition: background 0.2s; font-size: 1rem; margin-top: 8px; }
        button:hover { background: var(--primary-hover); }

        .footer { text-align: center; margin-top: 24px; font-size: 0.875rem; color: #64748b; }
        .link { color: var(--primary); text-decoration: none; font-weight: 600; }
        .link:hover { text-decoration: underline; }
    </style>
</head>
<body>
<div class="register-card">
    <h1>Créer un compte</h1>
    <p class="subtitle">Rejoignez la communauté MasterAnnonce</p>

    <c:if test="${not empty error}">
        <div class="error-alert">
            <i class="fas fa-circle-exclamation"></i>
            <span>${error}</span>
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/register">
        <div class="form-group">
            <label for="username">Nom d'utilisateur</label>
            <input type="text" id="username" name="username" required
                   placeholder="ex: lucas_dev" value="${param.username}"
                   class="${not empty error ? 'has-error' : ''}">
        </div>

        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" required
                   placeholder="votre@email.com" value="${param.email}">
        </div>

        <div class="form-group">
            <label for="password">Mot de passe</label>
            <input type="password" id="password" name="password" required
                   placeholder="••••••••">
        </div>

        <button type="submit">S'inscrire gratuitement</button>
    </form>

    <div class="footer">
        Déjà un compte ? <a href="${pageContext.request.contextPath}/login" class="link">Se connecter</a>
    </div>
</div>
</body>
</html>