package com.example.tp_air.servlets;

import com.example.tp_air.models.Annonce;
import com.example.tp_air.models.User;
import com.example.tp_air.services.AnnonceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/annonce/details")
public class AnnonceDetailsServlet extends HttpServlet {
    private AnnonceService annonceService;

    @Override
    public void init() { this.annonceService = new AnnonceService(); }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/annonce/list");
            return;
        }

        Long id = Long.parseLong(idParam);
        Annonce annonce = annonceService.findAnnonceById(id);

        if (annonce == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        User currentUser = (User) request.getSession().getAttribute("user");

        boolean isOwner = currentUser != null && annonce.getAuthor() != null
                && annonce.getAuthor().getId().equals(currentUser.getId());

        request.setAttribute("annonce", annonce);
        request.setAttribute("isOwner", isOwner);
        request.getRequestDispatcher("/AnnonceDetails.jsp").forward(request, response);
    }
}