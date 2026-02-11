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
                username.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Veuillez remplir tous les champs obligatoires.");
            request.getRequestDispatcher("/Register.jsp").forward(request, response);
            return;
        }

        try {
            User user = new User();
            user.setUsername(username.trim());
            user.setEmail(email.trim());
            user.setPassword(password);
            user.setCreated_at(Timestamp.from(Instant.now()));

            annonceService.createUser(user);

            request.getSession().setAttribute("success", "Compte créé ! Vous pouvez vous connecter.");
            response.sendRedirect(request.getContextPath() + "/login");

        } catch (Exception e) {
            String errorMessage;
            String technicalMsg = e.getMessage() != null ? e.getMessage() : "";

            if (technicalMsg.contains("users_username_key")) {
                errorMessage = "Ce nom d'utilisateur est déjà pris.";
            } else if (technicalMsg.contains("users_email_key")) {
                errorMessage = "Cet email est déjà associé à un compte.";
            } else {
                errorMessage = technicalMsg;
            }

            request.setAttribute("error", errorMessage);
            request.getRequestDispatcher("/Register.jsp").forward(request, response);
        }
    }
}