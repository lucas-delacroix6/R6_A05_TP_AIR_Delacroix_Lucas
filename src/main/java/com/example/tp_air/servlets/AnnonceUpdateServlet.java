package com.example.tp_air.servlets;

import com.example.tp_air.models.Annonce;
import com.example.tp_air.models.AnnonceStatus;
import com.example.tp_air.models.Category;
import com.example.tp_air.models.User;
import com.example.tp_air.services.AnnonceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/annonce/update")
public class AnnonceUpdateServlet extends HttpServlet {
    private AnnonceService annonceService;

    @Override
    public void init() { this.annonceService = new AnnonceService(); }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        User currentUser = (User) request.getSession().getAttribute("user");

        if (!annonceService.isAuthor(id, currentUser)) {
            request.getSession().setAttribute("error", "Accès refusé : vous n'êtes pas l'auteur de cette annonce.");
            response.sendRedirect(request.getContextPath() + "/annonce/list");
            return;
        }

        Annonce annonce = annonceService.findAnnonceById(id);
        request.setAttribute("annonce", annonce);
        request.setAttribute("categories", annonceService.findAllCategories());
        request.setAttribute("statuses", AnnonceStatus.values());
        request.getRequestDispatcher("/AnnonceUpdate.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Long id = Long.parseLong(request.getParameter("id"));
        User currentUser = (User) request.getSession().getAttribute("user");

        if (!annonceService.isAuthor(id, currentUser)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Annonce annonce = annonceService.findAnnonceById(id);
        annonce.setTitle(request.getParameter("title"));
        annonce.setDescription(request.getParameter("description"));
        annonce.setAddress(request.getParameter("address"));
        annonce.setMail(request.getParameter("mail"));
        annonce.setStatus(AnnonceStatus.valueOf(request.getParameter("status")));

        Category cat = annonceService.findCategoryById(Long.parseLong(request.getParameter("categoryId")));
        annonce.setCategory(cat);

        annonceService.updateAnnonce(annonce);
        request.getSession().setAttribute("success", "Annonce mise à jour avec succès !");
        response.sendRedirect(request.getContextPath() + "/annonce/list");
    }
}