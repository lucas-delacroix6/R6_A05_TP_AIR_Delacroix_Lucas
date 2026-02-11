package com.example.tp_air.servlets;

import com.example.tp_air.models.Annonce;
import com.example.tp_air.models.Category;
import com.example.tp_air.models.User;
import com.example.tp_air.services.AnnonceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/annonce/add")
public class AnnonceAddServlet extends HttpServlet {

    private AnnonceService annonceService;

    @Override
    public void init() {
        this.annonceService = new AnnonceService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("categories", annonceService.findAllCategories());
        request.getRequestDispatcher("/AnnonceAdd.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        String title = request.getParameter("title");
        String desc = request.getParameter("description");
        String address = request.getParameter("address");
        String mail = request.getParameter("mail");
        String categoryIdStr = request.getParameter("categoryId");

        if (title == null || title.trim().isEmpty() || categoryIdStr == null) {
            request.setAttribute("error", "Veuillez remplir les champs obligatoires");
            request.setAttribute("categories", annonceService.findAllCategories());
            request.getRequestDispatcher("/AnnonceAdd.jsp").forward(request, response);
            return;
        }

        try {
            Annonce annonce = new Annonce(title, desc, address, mail);

            Category category = annonceService.findCategoryById(Long.parseLong(categoryIdStr));
            annonce.setCategory(category);

            annonce.setAuthor(currentUser);

            annonceService.createAnnonce(annonce);
            response.sendRedirect(request.getContextPath() + "/annonce/list");
        } catch (Exception e) {
            request.setAttribute("error", "Erreur : " + e.getMessage());
            request.setAttribute("categories", annonceService.findAllCategories());
            request.getRequestDispatcher("/AnnonceAdd.jsp").forward(request, response);
        }
    }
}