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
import java.util.List;

@WebServlet("/annonce/list")
public class AnnonceListServlet extends HttpServlet {

    private AnnonceService annonceService;

    @Override
    public void init() {
        this.annonceService = new AnnonceService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String pageParam = request.getParameter("page");
        int page = (pageParam != null && !pageParam.isEmpty()) ? Integer.parseInt(pageParam) : 1;
        int size = 5;

        User currentUser = (User) request.getSession().getAttribute("user");

        List<Annonce> annonces = annonceService.searchAnnonces(keyword, page, size, currentUser);
        long totalAnnonces = annonceService.countAnnonces(keyword, currentUser);

        int totalPages = (int) Math.ceil((double) totalAnnonces / size);

        request.setAttribute("annonces", annonces);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("keyword", keyword);

        request.getRequestDispatcher("/AnnonceList.jsp").forward(request, response);
    }
}