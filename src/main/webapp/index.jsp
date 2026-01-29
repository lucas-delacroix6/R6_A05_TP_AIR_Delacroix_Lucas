<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
</head>
<body>
    <h1><%= "Hello World!" %></h1>
    <br/>
    <form action="hello-servlet" method="post">
        <label for="nom">Entrez votre nom :</label>
        <input type="text" id="nom" name="nom" required>
        <button type="submit">Valider</button>
    </form>
    <a href="annonce/list">Liste des annonces</a>
</body>
</html>