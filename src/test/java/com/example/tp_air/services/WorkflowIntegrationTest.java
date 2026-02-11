package com.example.tp_air.services;

import com.example.tp_air.models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class WorkflowIntegrationTest {

    @Test
    @DisplayName("Cycle de vie complet : Création -> Publication -> Recherche filtrée")
    void testCompleteLifeCycle() {
        AnnonceService service = new AnnonceService();

        List<User> users = service.findAllUsers();
        List<Category> cats = service.findAllCategories();

        if(users.isEmpty() || cats.isEmpty()) {
            fail("Base vide : ce test nécessite au moins un utilisateur et une catégorie existants.");
        }

        User author = users.getFirst();

        Annonce a = new Annonce("Appart Lyon", "Lumineux", "Lyon 06", "lyon@test.fr");
        a.setAuthor(author);
        a.setCategory(cats.getFirst());
        service.createAnnonce(a);

        assertNotNull(a.getId());
        assertEquals(AnnonceStatus.DRAFT, a.getStatus());

        service.publishAnnonce(a.getId(), author);

        Annonce updated = service.findAnnonceById(a.getId());
        assertEquals(AnnonceStatus.PUBLISHED, updated.getStatus());


        List<Annonce> list = service.searchAnnonces("Lyon", 1, 10, author);
        assertTrue(list.stream().anyMatch(res -> res.getTitle().contains("Appart Lyon")),
                "L'annonce devrait être visible pour son auteur");

        User stranger = new User();
        stranger.setId(-99L);

        List<Annonce> publicList = service.searchAnnonces("Lyon", 1, 10, stranger);
        assertTrue(publicList.stream().anyMatch(res -> res.getTitle().contains("Appart Lyon")),
                "L'annonce PUBLISHED doit être visible par tout le monde");
    }
}