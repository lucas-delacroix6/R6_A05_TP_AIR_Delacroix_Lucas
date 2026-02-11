package com.example.tp_air.servlets;

import com.example.tp_air.models.User;
import com.example.tp_air.services.AnnonceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private AnnonceService annonceService;

    @Override
    public void init() {
        this.annonceService = new AnnonceService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/Login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");

        List<User> users = annonceService.findAllUsers();
        User foundUser = users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (foundUser != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", foundUser);
            response.sendRedirect(request.getContextPath() + "/annonce/list");
        } else {
            request.setAttribute("error", "Utilisateur inconnu");
            request.getRequestDispatcher("/Login.jsp").forward(request, response);
        }
    }
}