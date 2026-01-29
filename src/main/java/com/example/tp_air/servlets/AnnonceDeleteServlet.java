package com.example.tp_air.servlets;

import com.example.tp_air.daos.AnnonceDAO;
import com.example.tp_air.utils.ConnectionDB;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;

@WebServlet("/annonce/delete")
public class AnnonceDeleteServlet extends HttpServlet {

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

            dao.delete(id);

            response.sendRedirect(request.getContextPath() + "/annonce/list");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/annonce/list");
        }
    }
}
