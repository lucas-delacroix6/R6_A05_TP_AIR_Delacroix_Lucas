package com.example.tp_air.services;

import com.example.tp_air.models.*;
import com.example.tp_air.repositories.AnnonceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AuthorizationTest {

    @Mock
    private AnnonceRepository annonceRepository;

    @InjectMocks
    private AnnonceService annonceService;
    @Test
    @DisplayName("Vérifie qu'un utilisateur ne peut pas modifier l'annonce d'un autre")
    void testAccessControl() {
        User user1 = new User(); user1.setId(1L);
        User user2 = new User(); user2.setId(2L);

        Annonce a = new Annonce();
        a.setAuthor(user1);

        when(annonceRepository.findById(100L)).thenReturn(a);

        assertFalse(annonceService.isAuthor(100L, user2));
        assertTrue(annonceService.isAuthor(100L, user1));
    }
}
