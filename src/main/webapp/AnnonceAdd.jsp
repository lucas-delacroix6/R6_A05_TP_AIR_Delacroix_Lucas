<%--
  Created by IntelliJ IDEA.
  User: lucas
  Date: 29/01/2026
  Time: 14:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Les Annonces - Nouvelle Annonce</title>

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
        --success: #10b981;
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

      .container {
        background: white;
        border-radius: 16px;
        width: 100%;
        max-width: 500px;
        padding: 40px;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
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
        letter-spacing: -0.025em;
      }

      .header p {
        color: var(--text-muted);
        font-size: 0.875rem;
      }

      /* Formulaire */
      .form-group {
        margin-bottom: 20px;
      }

      label {
        display: block;
        margin-bottom: 8px;
        font-weight: 600;
        font-size: 0.875rem;
        color: var(--text-main);
      }

      .input-wrapper {
        position: relative;
      }

      .input-wrapper i {
        position: absolute;
        left: 14px;
        top: 50%;
        transform: translateY(-50%);
        color: #cbd5e1;
        transition: color 0.2s;
      }

      .input-wrapper textarea + i {
        top: 20px; /* Aligné en haut pour les textareas */
        transform: none;
      }

      input[type="text"],
      input[type="email"],
      textarea {
        width: 100%;
        padding: 12px 12px 12px 42px; /* Espace pour l'icône à gauche */
        border: 1px solid #e2e8f0;
        border-radius: 8px;
        font-size: 0.95rem;
        font-family: inherit;
        transition: all 0.2s;
        box-sizing: border-box;
      }

      input:focus, textarea:focus {
        outline: none;
        border-color: var(--primary);
        box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
      }

      input:focus + i, textarea:focus + i {
        color: var(--primary);
      }

      textarea {
        resize: vertical;
        min-height: 100px;
      }

      /* Boutons */
      .btn-container {
        display: flex;
        flex-direction: column;
        gap: 12px;
        margin-top: 32px;
      }

      .btn {
        padding: 12px 24px;
        border-radius: 8px;
        font-weight: 600;
        font-size: 0.95rem;
        text-align: center;
        text-decoration: none;
        cursor: pointer;
        transition: all 0.2s;
        border: none;
      }

      .btn-primary {
        background-color: var(--primary);
        color: white;
      }

      .btn-primary:hover {
        background-color: var(--primary-hover);
      }

      .btn-secondary {
        background-color: transparent;
        color: var(--text-muted);
      }

      .btn-secondary:hover {
        background-color: #f1f5f9;
        color: var(--text-main);
      }

      /* Alertes */
      .alert {
        padding: 12px 16px;
        border-radius: 8px;
        font-size: 0.875rem;
        margin-bottom: 24px;
        display: flex;
        align-items: center;
        gap: 10px;
      }

      .alert-error { background: #fef2f2; color: var(--error); border: 1px solid #fee2e2; }
      .alert-success { background: #ecfdf5; color: var(--success); border: 1px solid #d1fae5; }
    </style>
  </head>
  <body>

    <div class="container">
      <div class="header">
        <h1>Déposer une annonce</h1>
        <p>Remplissez les détails de votre bien ci-dessous</p>
      </div>

      <%-- Messages de retour --%>
      <c:if test="${not empty error}">
        <div class="alert alert-error">
          <i class="fas fa-exclamation-circle"></i> ${error}
        </div>
      </c:if>

      <c:if test="${not empty success}">
        <div class="alert alert-success">
          <i class="fas fa-check-circle"></i> ${success}
        </div>
      </c:if>

      <form method="post" action="${pageContext.request.contextPath}/annonce/add">

        <div class="form-group">
          <label for="title">Titre de l'annonce</label>
          <div class="input-wrapper">
            <input type="text" id="title" name="title"
                   placeholder="Ex: Appartement T3 lumineux"
                   required maxlength="64"
                   value="${param.title}">
            <i class="fas fa-tag"></i>
          </div>
        </div>

        <div class="form-group">
          <label for="description">Description détaillée</label>
          <div class="input-wrapper">
                    <textarea id="description" name="description"
                              placeholder="Décrivez votre bien en quelques lignes..."
                              required maxlength="256">${param.description}</textarea>
            <i class="fas fa-align-left"></i>
          </div>
        </div>

        <div class="form-group">
          <label for="address">Localisation / Adresse</label>
          <div class="input-wrapper">
            <input type="text" id="address" name="address"
                   placeholder="Ex: Paris 13e, Italie"
                   required maxlength="64"
                   value="${param.address}">
            <i class="fas fa-map-marker-alt"></i>
          </div>
        </div>

        <div class="form-group">
          <label for="mail">Email de contact</label>
          <div class="input-wrapper">
            <input type="email" id="mail" name="mail"
                   placeholder="votre@email.com"
                   required maxlength="64"
                   value="${param.mail}">
            <i class="fas fa-envelope"></i>
          </div>
        </div>

        <div class="btn-container">
          <button type="submit" class="btn btn-primary">Publier l'annonce</button>
          <a href="${pageContext.request.contextPath}/annonce/list" class="btn btn-secondary">Annuler</a>
        </div>
      </form>
    </div>
  </body>
</html>
