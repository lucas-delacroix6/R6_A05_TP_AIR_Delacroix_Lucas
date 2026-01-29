package com.example.tp_air.servlets;

import com.example.tp_air.daos.AnnonceDAO;
import com.example.tp_air.models.Annonce;
import com.example.tp_air.utils.ConnectionDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.sql.Connection;
import java.util.List;

@WebServlet("/annonce/list")
public class AnnonceListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Connection conn = ConnectionDB.getInstance();
            AnnonceDAO dao = new AnnonceDAO(conn);
            List<Annonce> annonces = dao.findAll();

            request.setAttribute("annonces", annonces);
            request.getRequestDispatcher("/AnnonceList.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Erreur lors de la lecture des données.");
            request.getRequestDispatcher("/AnnonceList.jsp").forward(request, response);

        }
    }
}
