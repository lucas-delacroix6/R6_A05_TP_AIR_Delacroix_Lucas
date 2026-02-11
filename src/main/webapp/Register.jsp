<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Inscription</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Inter', sans-serif; background: #f8fafc; display: flex; align-items: center; justify-content: center; height: 100vh; margin: 0; }
        .register-card { background: white; padding: 40px; border-radius: 16px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); width: 100%; max-width: 400px; }
        h1 { font-size: 1.5rem; margin-bottom: 20px; text-align: center; color: #1e293b; }
        label { display: block; margin-bottom: 8px; font-size: 0.875rem; font-weight: 600; }
        input { width: 100%; padding: 12px; margin-bottom: 20px; border: 1px solid #e2e8f0; border-radius: 8px; box-sizing: border-box; }
        button { width: 100%; padding: 12px; background: #2563eb; color: white; border: none; border-radius: 8px; font-weight: 600; cursor: pointer; transition: background 0.2s; }
        button:hover { background: #1d4ed8; }
        .error { color: #ef4444; margin-bottom: 20px; font-size: 0.875rem; text-align: center; }
        .link { display: block; text-align: center; margin-top: 20px; font-size: 0.875rem; color: #64748b; text-decoration: none; }
        .link:hover { color: #2563eb; }
    </style>
</head>
<body>
<div class="register-card">
    <h1>Créer un compte</h1>
    <c:if test="${not empty error}"><div class="error">${error}</div></c:if>
    <form method="post" action="${pageContext.request.contextPath}/register">
        <label>Nom d'utilisateur</label>
        <input type="text" name="username" required value="${param.username}">

        <label>Email</label>
        <input type="email" name="email" required value="${param.email}">

        <label>Mot de passe</label>
        <input type="password" name="password" required>

        <button type="submit">S'inscrire</button>
    </form>
    <a href="${pageContext.request.contextPath}/login" class="link">Déjà un compte ? Se connecter</a>
</div>
</body>
</html>