<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${annonce.title} - Détails</title>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
  <style>
    :root {
      --primary: #2563eb;
      --primary-hover: #1d4ed8;
      --secondary: #64748b;
      --bg: #f8fafc;
      --text-main: #1e293b;
      --danger: #ef4444;
    }
    body {
      font-family: 'Inter', sans-serif;
      background-color: var(--bg);
      color: var(--text-main);
      margin: 0;
      padding: 40px 20px;
      display: flex;
      justify-content: center;
    }
    .container {
      max-width: 800px;
      width: 100%;
    }
    .back-link {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      text-decoration: none;
      color: var(--secondary);
      font-weight: 600;
      margin-bottom: 24px;
      transition: color 0.2s;
    }
    .back-link:hover { color: var(--primary); }
    .details-card {
      background: white;
      border-radius: 16px;
      border: 1px solid #e2e8f0;
      overflow: hidden;
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
    }
    .card-header {
      padding: 40px;
      background: linear-gradient(to right, #f8fafc, #fff);
      border-bottom: 1px solid #e2e8f0;
    }
    .badges {
      display: flex;
      gap: 10px;
      margin-bottom: 16px;
    }
    .badge {
      padding: 6px 12px;
      border-radius: 20px;
      font-size: 0.75rem;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }
    .badge-category { background: #dbeafe; color: var(--primary); }
    .badge-status { background: #f1f5f9; color: var(--secondary); border: 1px solid #e2e8f0; }
    .status-published { background-color: #dcfce7; color: #166534; border-color: #bbf7d0; }
    .status-archived { background-color: #ffedd5; color: #9a3412; border-color: #fed7aa; }
    h1 {
      font-size: 2rem;
      margin: 0 0 12px 0;
      color: var(--text-main);
      line-height: 1.2;
    }
    .meta-info {
      display: flex;
      align-items: center;
      gap: 20px;
      color: var(--secondary);
      font-size: 0.9rem;
    }
    .meta-info i { margin-right: 6px; }
    .card-body {
      padding: 40px;
    }
    h2 {
      font-size: 1.1rem;
      margin-bottom: 16px;
      color: var(--text-main);
      border-bottom: 2px solid #f1f5f9;
      padding-bottom: 8px;
      display: inline-block;
    }
    .description {
      font-size: 1.05rem;
      line-height: 1.7;
      color: #334155;
      margin-bottom: 40px;
      white-space: pre-wrap;
    }
    .contact-box {
      background: #f8fafc;
      border-radius: 12px;
      padding: 24px;
      display: flex;
      flex-direction: column;
      gap: 16px;
      border: 1px solid #e2e8f0;
    }
    .contact-item {
      display: flex;
      align-items: center;
      gap: 12px;
      font-size: 1rem;
    }
    .contact-icon {
      width: 40px;
      height: 40px;
      background: white;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--primary);
      box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    }
    .actions {
      margin-top: 40px;
      padding-top: 24px;
      border-top: 1px solid #e2e8f0;
      display: flex;
      justify-content: flex-end;
      gap: 12px;
    }
    .btn {
      padding: 10px 20px;
      border-radius: 8px;
      text-decoration: none;
      font-weight: 600;
      font-size: 0.9rem;
      display: inline-flex;
      align-items: center;
      gap: 8px;
      transition: all 0.2s;
    }
    .btn-edit { background: #f1f5f9; color: var(--text-main); }
    .btn-edit:hover { background: #e2e8f0; }
    .btn-delete { background: #fff1f2; color: var(--danger); }
    .btn-delete:hover { background: var(--danger); color: white; }
  </style>
</head>
<body>

<div class="container">
  <a href="${pageContext.request.contextPath}/annonce/list" class="back-link">
    <i class="fas fa-arrow-left"></i> Retour aux annonces
  </a>

  <c:if test="${not empty annonce}">
    <article class="details-card">

      <div class="card-header">
        <div class="badges">
          <c:if test="${not empty annonce.category}">
            <span class="badge badge-category">${annonce.category.label}</span>
          </c:if>
          <span class="badge badge-status ${annonce.status == 'PUBLISHED' ? 'status-published' : ''} ${annonce.status == 'ARCHIVED' ? 'status-archived' : ''}">
              ${annonce.status}
          </span>
        </div>

        <h1>${annonce.title}</h1>

        <div class="meta-info">
                    <span>
                        <i class="far fa-calendar"></i>
                        Publié le <fmt:formatDate value="${annonce.date}" pattern="dd MMMM yyyy" />
                    </span>
          <c:if test="${not empty annonce.author}">
                        <span>
                            <i class="far fa-user"></i>
                            Par <strong>${annonce.author.username}</strong>
                        </span>
          </c:if>
        </div>
      </div>

      <div class="card-body">

        <h2>Description</h2>
        <div class="description">
            ${annonce.description}
        </div>

        <h2>Informations de contact</h2>
        <div class="contact-box">
          <div class="contact-item">
            <div class="contact-icon"><i class="fas fa-map-marker-alt"></i></div>
            <div>
              <small style="color:var(--secondary)">Localisation</small><br>
              <strong>${annonce.address}</strong>
            </div>
          </div>
          <div class="contact-item">
            <div class="contact-icon"><i class="fas fa-envelope"></i></div>
            <div>
              <small style="color:var(--secondary)">Email de contact</small><br>
              <a href="mailto:${annonce.mail}" style="color:var(--primary); text-decoration:none;">
                <strong>${annonce.mail}</strong>
              </a>
            </div>
          </div>
        </div>

        <div class="actions">
          <c:if test="${isOwner}">
            <a href="${pageContext.request.contextPath}/annonce/update?id=${annonce.id}" class="btn btn-edit">Modifier</a>
            <a href="${pageContext.request.contextPath}/annonce/delete?id=${annonce.id}" class="btn btn-delete"
               onclick="return confirm('Supprimer cette annonce ?')">Supprimer</a>
          </c:if>
        </div>
      </div>
    </article>
  </c:if>

  <c:if test="${empty annonce}">
    <div style="text-align:center; padding: 40px;">
      <h2>Annonce introuvable</h2>
      <a href="${pageContext.request.contextPath}/annonce/list">Retour à la liste</a>
    </div>
  </c:if>
</div>

</body>
</html>