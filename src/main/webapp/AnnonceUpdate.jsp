<%--
  Created by IntelliJ IDEA.
  User: lucas
  Date: 29/01/2026
  Time: 15:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Les Annonces - Modifier l'Annonce</title>

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

            .container {
                background: white;
                border-radius: 16px;
                width: 100%;
                max-width: 500px;
                padding: 40px;
                box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
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
            }

            .input-wrapper textarea + i { top: 20px; transform: none; }

            input, textarea {
                width: 100%;
                padding: 12px 12px 12px 42px;
                border: 1px solid #e2e8f0;
                border-radius: 8px;
                font-size: 0.95rem;
                font-family: inherit;
                box-sizing: border-box;
                transition: all 0.2s;
            }

            input:focus, textarea:focus {
                outline: none;
                border-color: var(--primary);
                box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
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
                text-align: center;
                text-decoration: none;
                cursor: pointer;
                border: none;
                transition: all 0.2s;
            }

            .btn-primary { background: var(--primary); color: white; }
            .btn-primary:hover { background: var(--primary-hover); }

            .btn-secondary { background: transparent; color: var(--text-muted); }
            .btn-secondary:hover { background: #f1f5f9; color: var(--text-main); }

            .alert-error {
                padding: 12px 16px;
                background: #fef2f2;
                color: var(--error);
                border-radius: 8px;
                border: 1px solid #fee2e2;
                margin-bottom: 24px;
                font-size: 0.875rem;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header">
                <h1>Modifier l'annonce</h1>
                <p>Mettez à jour les informations de votre bien</p>
            </div>

            <c:if test="${not empty error}">
                <div class="alert-error">
                    <i class="fas fa-exclamation-circle"></i> ${error}
                </div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/annonce/update">
                <input type="hidden" name="id" value="${annonce.id}">

                <div class="form-group">
                    <label for="title">Titre</label>
                    <div class="input-wrapper">
                        <input type="text" id="title" name="title" value="${annonce.title}" required maxlength="64">
                        <i class="fas fa-tag"></i>
                    </div>
                </div>

                <div class="form-group">
                    <label for="description">Description</label>
                    <div class="input-wrapper">
                        <textarea id="description" name="description" required maxlength="256">${annonce.description}</textarea>
                        <i class="fas fa-align-left"></i>
                    </div>
                </div>

                <div class="form-group">
                    <label for="address">Adresse</label>
                    <div class="input-wrapper">
                        <input type="text" id="address" name="address" value="${annonce.address}" required maxlength="64">
                        <i class="fas fa-map-marker-alt"></i>
                    </div>
                </div>

                <div class="form-group">
                    <label for="mail">Email de contact</label>
                    <div class="input-wrapper">
                        <input type="email" id="mail" name="mail" value="${annonce.mail}" required maxlength="64">
                        <i class="fas fa-envelope"></i>
                    </div>
                </div>

                <div class="btn-container">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save"></i> Enregistrer les modifications
                    </button>
                    <a href="${pageContext.request.contextPath}/annonce/list" class="btn btn-secondary">Annuler</a>
                </div>
            </form>
        </div>
    </body>
</html>