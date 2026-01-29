package com.example.tp_air.servlets;

import java.io.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<h1>Hello the World !</h1>");
        out.println("<p>Utilisez le formulaire pour personnaliser ce message.</p>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // 1. Récupération du paramètre "nom" envoyé par la JSP
        String nom = request.getParameter("nom");

        // 2. Préparation de la réponse
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // 3. Affichage du résultat
        out.println("<html><body>");
        if (nom != null && !nom.isEmpty()) {
            out.println("<h1>Hello the World " + nom + " !</h1>");
        } else {
            out.println("<h1>Hello the World !</h1>");
        }
        out.println("<a href='index.jsp'>Retour</a>");
        out.println("</body></html>");
    }

    public void destroy() {
    }
}