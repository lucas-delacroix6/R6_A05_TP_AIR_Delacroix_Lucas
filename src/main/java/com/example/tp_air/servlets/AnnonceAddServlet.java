package com.example.tp_air.servlets;

import com.example.tp_air.daos.AnnonceDAO;
import com.example.tp_air.models.Annonce;
import com.example.tp_air.utils.ConnectionDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/annonce/add")
public class AnnonceAddServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/AnnonceAdd.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String title = request.getParameter("title");
        String desc = request.getParameter("description");
        String address = request.getParameter("address");
        String mail = request.getParameter("mail");

        if (title == null || title.trim().isEmpty() ||
                desc == null  || desc.trim().isEmpty() ||
                address == null || address.trim().isEmpty() ||
                mail == null || mail.trim().isEmpty()
        ) {

            request.setAttribute("error", "Tous les champs sont obligatoires");
            request.getRequestDispatcher("/AnnonceAdd.jsp").forward(request, response);
            return;
        }

        try {
            Annonce annonce = new Annonce(title, desc, address, mail);

            Connection conn = ConnectionDB.getInstance();
            AnnonceDAO dao = new AnnonceDAO(conn);

            if (dao.create(annonce)) {
                response.sendRedirect(request.getContextPath() + "/annonce/list");
            } else {
                request.setAttribute("error", "Erreur lors de la creation");
                request.getRequestDispatcher("/AnnonceAdd.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Erreur BDD : " + e.getMessage());
            request.getRequestDispatcher("/AnnonceAdd.jsp").forward(request, response);

        }
    }
}
