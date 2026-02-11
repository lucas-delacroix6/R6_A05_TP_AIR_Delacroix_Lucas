package com.example.tp_air.servlets;

import com.example.tp_air.models.Annonce;
import com.example.tp_air.models.User;
import com.example.tp_air.services.AnnonceService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/annonce/delete")
public class AnnonceDeleteServlet extends HttpServlet {

    private AnnonceService annonceService;

    @Override
    public void init() {
        this.annonceService = new AnnonceService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idParam = request.getParameter("id");
        HttpSession session = request.getSession();

        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/annonce/list");
            return;
        }

        try {
            long id = Long.parseLong(request.getParameter("id"));
            User currentUser = (User) request.getSession().getAttribute("user");

            if (annonceService.isAuthor(id, currentUser)) {
                id = Long.parseLong(idParam);
                Annonce annonce = annonceService.findAnnonceById(id);

                if (annonce != null) {
                    annonceService.deleteAnnonce(annonce);
                    session.setAttribute("success", "L'annonce a bien été supprimée.");
                } else {
                    session.setAttribute("error", "Impossible de trouver cette annonce.");
                }
            }
            else {
                request.getSession().setAttribute("error", "Action interdite : vous n'êtes pas l'auteur.");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("error", "Format de l'ID invalide.");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Erreur lors de la suppression.");
        }
        response.sendRedirect(request.getContextPath() + "/annonce/list");
    }
}