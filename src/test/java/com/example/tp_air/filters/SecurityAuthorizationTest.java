package com.example.tp_air.filters;

import com.example.tp_air.models.Annonce;
import com.example.tp_air.models.User;
import com.example.tp_air.repositories.AnnonceRepository;
import com.example.tp_air.services.AnnonceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityAuthorizationTest {

    @Mock
    private AnnonceRepository annonceRepository;

    @InjectMocks
    private AnnonceService annonceService;

    @Test
    @DisplayName("Vérifie l'interdiction de modifier l'annonce d'autrui")
    void testIllegalAccessControl() {
        User author = new User(); author.setId(10L);
        User hacker = new User(); hacker.setId(99L);

        Annonce annonce = new Annonce();
        annonce.setId(1L);
        annonce.setAuthor(author);

        when(annonceRepository.findById(1L)).thenReturn(annonce);

        boolean isAuthorized = annonceService.isAuthor(1L, hacker);

        assertFalse(isAuthorized, "Le pirate (99) ne doit pas être autorisé pour l'annonce de l'auteur (10)");
        assertTrue(annonceService.isAuthor(1L, author), "L'auteur réel doit être autorisé");
    }
}