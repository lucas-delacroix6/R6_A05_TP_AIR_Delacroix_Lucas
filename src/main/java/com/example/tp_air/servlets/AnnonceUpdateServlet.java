package com.example.tp_air.servlets;


import com.example.tp_air.daos.AnnonceDAO;
import com.example.tp_air.models.Annonce;
import com.example.tp_air.utils.ConnectionDB;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;

@WebServlet("/annonce/update")
public class AnnonceUpdateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/annonce/list");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);

            Connection conn = ConnectionDB.getInstance();
            AnnonceDAO dao = new AnnonceDAO(conn);
            Annonce annonce = dao.find(id);

            if (annonce != null) {
                request.setAttribute("annonce", annonce);
                request.getRequestDispatcher("/AnnonceUpdate.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/annonce/list");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/annonce/list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("id");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String address = request.getParameter("address");
        String mail = request.getParameter("mail");

        if (title == null || title.trim().isEmpty() ||
        description == null || description.trim().isEmpty() ||
        address == null || address.trim().isEmpty() ||
        mail == null || mail.trim().isEmpty()) {

            request.setAttribute("error", "Tous les champs sont obligatoires");
            doGet(request, response);
            return;
        }

        try {
            int id = Integer.parseInt(idParam);

            Annonce annonce = new Annonce(title, description, address, mail);
            annonce.setId(id);

            Connection conn = ConnectionDB.getInstance();
            AnnonceDAO dao = new AnnonceDAO(conn);

            if (dao.update(annonce)) {
                response.sendRedirect(request.getContextPath() + "/annonce/list");
            } else {
                request.setAttribute("error", "Erreur lors de la mise a jour");
                doGet(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur serveur");
            doGet(request, response);
        }
    }
}
