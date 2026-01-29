<%--
  Created by IntelliJ IDEA.
  User: lucas
  Date: 29/01/2026
  Time: 14:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Les Annonces - Liste</title>

        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

        <style>
            :root {
                --primary: #2563eb;
                --primary-hover: #1d4ed8;
                --danger: #ef4444;
                --secondary: #64748b;
                --bg: #f8fafc;
                --text-main: #1e293b;
                --text-muted: #64748b;
            }

            body {
                font-family: 'Inter', sans-serif;
                background-color: var(--bg);
                color: var(--text-main);
                margin: 0;
                padding: 40px 20px 100px 20px; /* Padding bottom pour ne pas cacher la dernière carte par le bouton */
                line-height: 1.5;
            }

            .container {
                max-width: 800px;
                margin: 0 auto;
            }

            /* Header simplifié */
            .header {
                margin-bottom: 40px;
                text-align: center;
            }

            h1 {
                font-size: 2.25rem;
                font-weight: 800;
                color: var(--text-main);
                margin: 0;
                letter-spacing: -0.05em;
            }

            /* Bouton Flottant (FAB) */
            .fab {
                position: fixed;
                bottom: 40px;
                right: 40px;
                width: 64px;
                height: 64px;
                background-color: var(--primary);
                color: white;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                text-decoration: none;
                box-shadow: 0 10px 25px -5px rgba(37, 99, 235, 0.4);
                transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                z-index: 1000;
            }

            .fab:hover {
                transform: scale(1.1);
                background-color: var(--primary-hover);
                box-shadow: 0 20px 25px -5px rgba(37, 99, 235, 0.5);
            }

            /* Tooltip pour le bouton flottant */
            .fab::after {
                content: "Nouvelle annonce";
                position: absolute;
                right: 80px;
                background: #1e293b;
                color: white;
                padding: 8px 12px;
                border-radius: 6px;
                font-size: 0.75rem;
                white-space: nowrap;
                opacity: 0;
                pointer-events: none;
                transition: opacity 0.2s ease;
            }

            .fab:hover::after { opacity: 1; }

            /* Cartes d'annonces */
            .annonce-card {
                background: white;
                border-radius: 16px;
                padding: 28px;
                margin-bottom: 24px;
                border: 1px solid #e2e8f0;
                transition: all 0.3s ease;
            }

            .annonce-card:hover {
                border-color: var(--primary);
                box-shadow: 0 10px 30px -10px rgba(0,0,0,0.05);
            }

            .annonce-header {
                display: flex;
                justify-content: space-between;
                align-items: flex-start;
                margin-bottom: 16px;
            }

            .annonce-title {
                font-size: 1.4rem;
                font-weight: 700;
                color: var(--text-main);
                margin: 0;
            }

            .annonce-date {
                font-size: 0.8rem;
                color: var(--text-muted);
                font-weight: 600;
                text-transform: uppercase;
                letter-spacing: 0.05em;
            }

            .annonce-description {
                color: #475569;
                margin-bottom: 24px;
                font-size: 1rem;
            }

            .annonce-info {
                display: flex;
                gap: 30px;
                margin-bottom: 24px;
                padding-top: 16px;
                border-top: 1px solid #f1f5f9;
            }

            .info-item {
                display: flex;
                align-items: center;
                gap: 10px;
                font-size: 0.9rem;
                color: var(--text-muted);
            }

            .info-item i { color: var(--primary); }

            .annonce-actions {
                display: flex;
                gap: 12px;
            }

            .btn {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 8px 16px;
                border-radius: 8px;
                text-decoration: none;
                font-weight: 600;
                font-size: 0.85rem;
                transition: all 0.2s;
            }

            .btn-edit { background: #f1f5f9; color: var(--text-main); }
            .btn-edit:hover { background: #e2e8f0; }

            .btn-delete { background: #fff1f2; color: var(--danger); }
            .btn-delete:hover { background: var(--danger); color: white; }

            .empty-state {
                background: white;
                border-radius: 16px;
                padding: 80px 40px;
                text-align: center;
                border: 2px dashed #cbd5e1;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <header class="header">
                <h1>Les Annonces</h1>
                <p style="color: var(--text-muted)">Trouvez ou déposez votre prochaine annonce</p>
            </header>

            <%-- Message d'erreur --%>
            <c:if test="${not empty error}">
                <div style="padding: 16px; background: #fef2f2; border: 1px solid #fee2e2; border-radius: 12px; margin-bottom: 30px; color: var(--danger);">
                    <i class="fas fa-exclamation-circle"></i> ${error}
                </div>
            </c:if>

            <c:choose>
                <c:when test="${not empty annonces}">
                    <c:forEach var="annonce" items="${annonces}">
                        <article class="annonce-card">
                            <div class="annonce-header">
                                <h2 class="annonce-title">${annonce.title}</h2>
                                <span class="annonce-date">
                                    Créée le <fmt:formatDate value="${annonce.date}" pattern="dd/MM/yyyy HH:MM" />
                                </span>
                            </div>

                            <p class="annonce-description">${annonce.description}</p>

                            <div class="annonce-info">
                                <div class="info-item">
                                    <i class="fas fa-map-marker-alt"></i> ${annonce.address}
                                </div>
                                <div class="info-item">
                                    <i class="fas fa-envelope"></i> ${annonce.mail}
                                </div>
                            </div>

                            <div class="annonce-actions">
                                <a href="${pageContext.request.contextPath}/annonce/update?id=${annonce.id}" class="btn btn-edit">
                                    <i class="fas fa-pen"></i> Modifier
                                </a>
                                <a href="${pageContext.request.contextPath}/annonce/delete?id=${annonce.id}" class="btn btn-delete" onclick="return confirm('Supprimer définitivement ?');">
                                    <i class="fas fa-trash-alt"></i>
                                </a>
                            </div>
                        </article>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <i class="fas fa-folder-open" style="font-size: 3rem; color: #e2e8f0; margin-bottom: 20px;"></i>
                        <h2>Aucune annonce pour le moment</h2>
                        <p>Cliquez sur le bouton + pour commencer.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <a href="${pageContext.request.contextPath}/annonce/add" class="fab">
            <i class="fas fa-plus"></i>
        </a>
    </body>
</html>