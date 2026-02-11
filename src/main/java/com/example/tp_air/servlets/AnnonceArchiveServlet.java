package com.example.tp_air.servlets;

import com.example.tp_air.models.User;
import com.example.tp_air.services.AnnonceService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/annonce/archive")
public class AnnonceArchiveServlet extends HttpServlet {

    private AnnonceService annonceService;

    @Override
    public void init() {
        this.annonceService = new AnnonceService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");

        if (idParam != null && !idParam.isEmpty()) {
            try {
                Long id = Long.parseLong(request.getParameter("id"));
                User user = (User) request.getSession().getAttribute("user");

                annonceService.publishAnnonce(id, user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        response.sendRedirect(request.getContextPath() + "/annonce/list");
    }
}