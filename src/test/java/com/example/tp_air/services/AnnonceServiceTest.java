package com.example.tp_air.services;

import com.example.tp_air.models.*;
import com.example.tp_air.repositories.AnnonceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AnnonceServiceTest {

    @Mock
    private AnnonceRepository annonceRepository;

    @InjectMocks
    private AnnonceService annonceService;

    @Test
    @DisplayName("Succès : Archive une annonce dont on est l'auteur")
    void testArchiveAnnonceLogic() {
        User author = new User(); author.setId(10L);
        Annonce a = new Annonce();
        a.setId(1L);
        a.setAuthor(author);
        a.setStatus(AnnonceStatus.PUBLISHED);

        when(annonceRepository.findById(1L)).thenReturn(a);

        annonceService.archiveAnnonce(1L, author);

        assertEquals(AnnonceStatus.ARCHIVED, a.getStatus(), "Le statut doit passer à ARCHIVED");
        verify(annonceRepository).update(a);
    }

    @Test
    @DisplayName("Sécurité : Refuse d'archiver une annonce d'un autre auteur")
    void testArchiveUnauthorized() {
        User author = new User(); author.setId(10L);
        User hacker = new User(); hacker.setId(99L);

        Annonce a = new Annonce();
        a.setId(1L);
        a.setAuthor(author);
        a.setStatus(AnnonceStatus.PUBLISHED);

        when(annonceRepository.findById(1L)).thenReturn(a);

        annonceService.archiveAnnonce(1L, hacker);

        assertNotEquals(AnnonceStatus.ARCHIVED, a.getStatus());
        verify(annonceRepository, never()).update(any());
    }

    @Test
    @DisplayName("Visibilité : Vérifie que l'ID de l'utilisateur est bien transmis au repo pour le filtrage")
    void testSearchVisibilityInService() {
        User user = new User(); user.setId(1L);
        String kw = "test";

        annonceService.searchAnnonces(kw, 1, 10, user);

        verify(annonceRepository).search(eq(kw), eq(1), eq(10), eq(1L));
    }
}