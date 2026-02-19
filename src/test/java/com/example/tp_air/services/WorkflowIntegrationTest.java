package com.example.tp_air.services;

import com.example.tp_air.dto.AnnonceDTO;
import com.example.tp_air.dto.PatchAnnonceDTO;
import com.example.tp_air.models.Annonce;
import com.example.tp_air.models.User;
import com.example.tp_air.repositories.AnnonceRepository;
import com.example.tp_air.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkflowIntegrationTest {

    @Mock
    private AnnonceRepository annonceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AnnonceService annonceService;

    @Test
    @DisplayName("Cycle de vie complet : Création -> Publication -> Vérification")
    void testCompleteLifeCycle() {
        // --- Données de test ---
        User author = new User();
        author.setId(1L);
        author.setUsername("testeur");

        Annonce savedAnnonce = Annonce.builder()
                .title("Appart Lyon")
                .description("Lumineux")
                .price(new BigDecimal("500.00"))
                .category("Immobilier")
                .status(Annonce.Status.DRAFT)
                .author(author)
                .build();
        savedAnnonce.setId(10L);

        // --- 1. Création ---
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(annonceRepository.save(any(Annonce.class))).thenReturn(savedAnnonce);

        AnnonceDTO createInput = AnnonceDTO.builder()
                .title("Appart Lyon")
                .description("Lumineux")
                .price(new BigDecimal("500.00"))
                .category("Immobilier")
                .build();

        AnnonceDTO created = annonceService.create(createInput, 1L);

        assertNotNull(created.getId());
        assertEquals("Appart Lyon", created.getTitle());

        // --- 2. Publication via patch (DRAFT -> PUBLISHED) ---
        Annonce draftAnnonce = Annonce.builder()
                .title("Appart Lyon")
                .description("Lumineux")
                .price(new BigDecimal("500.00"))
                .category("Immobilier")
                .status(Annonce.Status.DRAFT)
                .author(author)
                .build();
        draftAnnonce.setId(10L);

        Annonce publishedAnnonce = Annonce.builder()
                .title("Appart Lyon")
                .description("Lumineux")
                .price(new BigDecimal("500.00"))
                .category("Immobilier")
                .status(Annonce.Status.PUBLISHED)
                .author(author)
                .build();
        publishedAnnonce.setId(10L);

        when(annonceRepository.findById(10L)).thenReturn(Optional.of(draftAnnonce));
        when(annonceRepository.update(any(Annonce.class))).thenReturn(publishedAnnonce);

        PatchAnnonceDTO patchDTO = new PatchAnnonceDTO();
        patchDTO.setStatus("PUBLISHED");

        AnnonceDTO patched = annonceService.patch(10L, patchDTO, 1L);

        assertEquals("PUBLISHED", patched.getStatus());

        // --- 3. Vérification que l'annonce est bien publiée ---
        when(annonceRepository.findById(10L)).thenReturn(Optional.of(publishedAnnonce));

        AnnonceDTO found = annonceService.findById(10L);

        assertEquals(10L, found.getId());
        assertEquals("PUBLISHED", found.getStatus());
        assertEquals("Appart Lyon", found.getTitle());
    }
}