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
            --success: #10b981;
            --warning: #f59e0b;
        }
        body {
            font-family: 'Inter', sans-serif;
            background-color: var(--bg);
            color: var(--text-main);
            margin: 0;
            padding: 40px 20px 100px 20px;
            line-height: 1.5;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
        }
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
        .search-bar {
            margin-bottom: 30px;
            display: flex;
            gap: 10px;
        }
        .search-input {
            flex: 1;
            padding: 12px;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            font-size: 1rem;
        }
        .btn-search {
            background-color: var(--text-main);
            color: white;
            padding: 0 20px;
            border-radius: 8px;
            border: none;
            cursor: pointer;
            font-weight: 600;
        }
        .category-badge {
            background-color: #e0e7ff;
            color: var(--primary);
            font-size: 0.75rem;
            padding: 4px 10px;
            border-radius: 20px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            vertical-align: middle;
            margin-left: 10px;
        }
        .status-badge {
            background-color: #f1f5f9;
            color: var(--secondary);
            font-size: 0.75rem;
            padding: 4px 10px;
            border-radius: 20px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            vertical-align: middle;
            margin-left: 8px;
            border: 1px solid #e2e8f0;
        }
        .status-published {
            background-color: #dcfce7;
            color: #166534;
            border-color: #bbf7d0;
        }
        .status-archived {
            background-color: #ffedd5;
            color: #9a3412;
            border-color: #fed7aa;
        }
        .author-info {
            font-weight: 600;
            color: var(--primary);
        }
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
            display: flex;
            align-items: center;
            flex-wrap: wrap;
            gap: 5px;
        }
        .title-link {
            text-decoration: none;
            color: inherit;
            transition: color 0.2s;
        }
        .title-link:hover {
            color: var(--primary);
            text-decoration: underline;
        }
        .annonce-meta {
            display: flex;
            flex-direction: column;
            align-items: flex-end;
            gap: 4px;
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
            align-items: center;
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
        .btn-view {
            background-color: var(--primary);
            color: white;
            border: 1px solid var(--primary);
        }
        .btn-view:hover {
            background-color: var(--primary-hover);
            transform: translateY(-1px);
            box-shadow: 0 4px 6px -1px rgba(37, 99, 235, 0.2);
        }
        .btn-edit { background: #f1f5f9; color: var(--text-main); border: 1px solid #cbd5e1; }
        .btn-edit:hover { background: #e2e8f0; border-color: #94a3b8; }
        .btn-delete { background: #fff1f2; color: var(--danger); border: 1px solid #fee2e2; }
        .btn-delete:hover { background: var(--danger); color: white; }
        .btn-publish { background-color: #ecfdf5; color: #10b981; border: 1px solid #d1fae5; }
        .btn-publish:hover { background-color: #10b981; color: white; }
        .btn-archive { background-color: #fff7ed; color: #f59e0b; border: 1px solid #ffedd5; }
        .btn-archive:hover { background-color: #f59e0b; color: white; }
        .empty-state {
            background: white;
            border-radius: 16px;
            padding: 80px 40px;
            text-align: center;
            border: 2px dashed #cbd5e1;
        }
        .pagination {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 8px;
            margin-top: 40px;
            padding-bottom: 20px;
        }
        .page-link {
            padding: 8px 16px;
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            text-decoration: none;
            color: var(--text-main);
            font-weight: 600;
            transition: all 0.2s;
        }
        .page-link:hover {
            border-color: var(--primary);
            color: var(--primary);
            background: #eff6ff;
        }
        .page-link.active {
            background: var(--primary);
            color: white;
            border-color: var(--primary);
        }
        .flash-message {
            transition: opacity 0.5s ease, margin 0.5s ease, padding 0.5s ease;
        }
    </style>
</head>
<body>
<div class="container">
    <header class="header">
        <div style="text-align: right; font-size: 0.9rem;">
            Bienvenue, <strong>${sessionScope.user.username}</strong> |
            <a href="${pageContext.request.contextPath}/logout" style="color: var(--danger);">Déconnexion</a>
        </div>
        <h1>Les Annonces</h1>
        <p style="color: var(--text-muted)">Trouvez ou déposez votre prochaine annonce</p>
    </header>

    <form class="search-bar" action="${pageContext.request.contextPath}/annonce/list" method="get">
        <input type="text" name="keyword" class="search-input" placeholder="Rechercher une annonce..." value="${keyword}">
        <button type="submit" class="btn-search"><i class="fas fa-search"></i></button>
    </form>

    <c:if test="${not empty sessionScope.success}">
        <div class="flash-message" style="padding: 16px; background: #ecfdf5; border: 1px solid #d1fae5; border-radius: 12px; margin-bottom: 30px; color: #10b981; display: flex; align-items: center; gap: 10px;">
            <i class="fas fa-check-circle"></i> ${sessionScope.success}
        </div>
        <c:remove var="success" scope="session"/>
    </c:if>

    <c:if test="${not empty sessionScope.error}">
        <div class="flash-message" style="padding: 16px; background: #fef2f2; border: 1px solid #fee2e2; border-radius: 12px; margin-bottom: 30px; color: #ef4444; display: flex; align-items: center; gap: 10px;">
            <i class="fas fa-exclamation-circle"></i> ${sessionScope.error}
        </div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <c:choose>
        <c:when test="${not empty annonces}">
            <c:forEach var="annonce" items="${annonces}">
                <article class="annonce-card">
                    <div class="annonce-header">
                        <h2 class="annonce-title">
                            <a href="${pageContext.request.contextPath}/annonce/details?id=${annonce.id}" class="title-link">
                                    ${annonce.title}
                            </a>
                            <c:if test="${not empty annonce.category}">
                                <span class="category-badge">${annonce.category.label}</span>
                            </c:if>
                            <span class="status-badge ${annonce.status == 'PUBLISHED' ? 'status-published' : ''} ${annonce.status == 'ARCHIVED' ? 'status-archived' : ''}">
                                    ${annonce.status}
                            </span>
                        </h2>
                        <div class="annonce-meta">
                            <span class="annonce-date">
                                <i class="far fa-calendar"></i> <fmt:formatDate value="${annonce.date}" pattern="dd/MM/yyyy" />
                            </span>
                            <c:if test="${not empty annonce.author}">
                                <span class="annonce-date" style="text-transform: none;">
                                    par <span class="author-info">${annonce.author.username}</span>
                                </span>
                            </c:if>
                        </div>
                    </div>

                    <p class="annonce-description">
                        <c:choose>
                            <c:when test="${annonce.description.length() > 100}">
                                ${annonce.description.substring(0, 100)}...
                            </c:when>
                            <c:otherwise>${annonce.description}</c:otherwise>
                        </c:choose>
                    </p>

                    <div class="annonce-info">
                        <div class="info-item">
                            <i class="fas fa-map-marker-alt"></i> ${annonce.address}
                        </div>
                        <div class="info-item">
                            <i class="fas fa-envelope"></i> ${annonce.mail}
                        </div>
                    </div>

                    <div class="annonce-actions">
                        <a href="${pageContext.request.contextPath}/annonce/details?id=${annonce.id}" class="btn btn-view">
                            <i class="fas fa-eye"></i> Consulter
                        </a>

                        <c:if test="${annonce.author.id == sessionScope.user.id}">
                            <c:if test="${annonce.status == 'DRAFT'}">
                                <a href="${pageContext.request.contextPath}/annonce/publish?id=${annonce.id}" class="btn btn-publish" title="Publier">
                                    <i class="fas fa-bullhorn"></i>
                                </a>
                            </c:if>

                            <c:if test="${annonce.status == 'PUBLISHED'}">
                                <a href="${pageContext.request.contextPath}/annonce/archive?id=${annonce.id}" class="btn btn-archive" title="Archiver">
                                    <i class="fas fa-box-archive"></i>
                                </a>
                            </c:if>

                            <a href="${pageContext.request.contextPath}/annonce/update?id=${annonce.id}" class="btn btn-edit">
                                <i class="fas fa-pen"></i>
                            </a>

                            <a href="${pageContext.request.contextPath}/annonce/delete?id=${annonce.id}" class="btn btn-delete"
                               onclick="return confirm('Supprimer définitivement ?');">
                                <i class="fas fa-trash-alt"></i>
                            </a>
                        </c:if>
                    </div>
                </article>
            </c:forEach>

            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a href="?page=${currentPage - 1}&keyword=${keyword}" class="page-link">
                        <i class="fas fa-chevron-left"></i> Précédent
                    </a>
                </c:if>

                <c:forEach begin="1" end="${totalPages}" var="i">
                    <a href="?page=${i}&keyword=${keyword}"
                       class="page-link ${i == currentPage ? 'active' : ''}">
                            ${i}
                    </a>
                </c:forEach>

                <c:if test="${currentPage < totalPages}">
                    <a href="?page=${currentPage + 1}&keyword=${keyword}" class="page-link">
                        Suivant <i class="fas fa-chevron-right"></i>
                    </a>
                </c:if>
            </div>
        </c:when>
        <c:otherwise>
            <div class="empty-state">
                <i class="fas fa-folder-open" style="font-size: 3rem; color: #e2e8f0; margin-bottom: 20px;"></i>
                <h2>Aucune annonce trouvée</h2>
                <p>Modifiez votre recherche ou cliquez sur le bouton + pour commencer.</p>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<a href="${pageContext.request.contextPath}/annonce/add" class="fab">
    <i class="fas fa-plus"></i>
</a>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        const messages = document.querySelectorAll('.flash-message');
        if (messages.length > 0) {
            setTimeout(function() {
                messages.forEach(function(msg) {
                    msg.style.opacity = "0";
                    msg.style.marginTop = "0";
                    msg.style.marginBottom = "0";
                    msg.style.paddingTop = "0";
                    msg.style.paddingBottom = "0";
                    msg.style.height = "0";
                    msg.style.border = "none";
                    msg.style.overflow = "hidden";
                    setTimeout(function() { msg.remove(); }, 500);
                });
            }, 3000);
        }
    });
</script>
</body>
</html>