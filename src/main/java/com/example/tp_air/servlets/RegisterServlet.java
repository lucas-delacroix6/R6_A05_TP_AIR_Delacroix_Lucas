package com.example.tp_air.servlets;

import com.example.tp_air.models.User;
import com.example.tp_air.services.AnnonceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private AnnonceService annonceService;

    @Override
    public void init() {
        this.annonceService = new AnnonceService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/Register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (username == null || email == null || password == null ||
                username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            request.setAttribute("error", "Tous les champs sont obligatoires");
            request.getRequestDispatcher("/Register.jsp").forward(request, response);
            return;
        }

        try {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setCreated_at(Timestamp.from(Instant.now()));

            annonceService.createUser(user);

            response.sendRedirect(request.getContextPath() + "/login");
        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors de l'inscription : " + e.getMessage());
            request.getRequestDispatcher("/Register.jsp").forward(request, response);
        }
    }
}