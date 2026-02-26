package com.example.tp_air.services;

import com.example.tp_air.dto.AnnonceDTO;
import com.example.tp_air.dto.PatchAnnonceDTO;
import com.example.tp_air.mappers.AnnonceMapper;
import com.example.tp_air.models.Annonce;
import com.example.tp_air.models.AnnonceStatus;
import com.example.tp_air.models.Category;
import com.example.tp_air.models.User;
import com.example.tp_air.repositories.AnnonceRepository;
import com.example.tp_air.repositories.CategoryRepository;
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

        @Mock
        private CategoryRepository categoryRepository;

        @Mock
        private AnnonceMapper annonceMapper;

        @InjectMocks
        private AnnonceService annonceService;

        @Test
        @DisplayName("Cycle de vie complet : Création -> Publication -> Vérification")
        void testCompleteLifeCycle() {

                User author = new User();
                author.setId(1L);
                author.setUsername("testeur");

                Category testCategory = new Category();
                testCategory.setId(1L);
                testCategory.setLabel("Immobilier");

                Annonce savedAnnonce = Annonce.builder()
                                .title("Appart Lyon")
                                .description("Lumineux")
                                .price(new BigDecimal("500.00"))
                                .category(testCategory)
                                .status(AnnonceStatus.DRAFT)
                                .author(author)
                                .build();
                savedAnnonce.setId(10L);

                when(userRepository.findById(1L)).thenReturn(Optional.of(author));
                when(categoryRepository.findByLabel("Immobilier")).thenReturn(Optional.of(testCategory));
                when(annonceRepository.save(any(Annonce.class))).thenReturn(savedAnnonce);

                AnnonceDTO createInput = AnnonceDTO.builder()
                                .title("Appart Lyon")
                                .description("Lumineux")
                                .price(new BigDecimal("500.00"))
                                .category("Immobilier")
                                .build();

                AnnonceDTO expectedCreated = AnnonceDTO.builder()
                                .id(10L)
                                .title("Appart Lyon")
                                .description("Lumineux")
                                .price(new BigDecimal("500.00"))
                                .category("Immobilier")
                                .status("DRAFT")
                                .build();

                // mock toEntity
                when(annonceMapper.toEntity(any(AnnonceDTO.class))).thenReturn(savedAnnonce);
                // mock toDto
                when(annonceMapper.toDto(any(Annonce.class))).thenReturn(expectedCreated);

                AnnonceDTO createdActual = annonceService.create(createInput, 1L);

                assertNotNull(createdActual.getId());
                assertEquals("Appart Lyon", createdActual.getTitle());

                Annonce draftAnnonce = Annonce.builder()
                                .title("Appart Lyon")
                                .description("Lumineux")
                                .price(new BigDecimal("500.00"))
                                .category(testCategory)
                                .status(AnnonceStatus.DRAFT)
                                .author(author)
                                .build();
                draftAnnonce.setId(10L);

                Annonce publishedAnnonce = Annonce.builder()
                                .title("Appart Lyon")
                                .description("Lumineux")
                                .price(new BigDecimal("500.00"))
                                .category(testCategory)
                                .status(AnnonceStatus.PUBLISHED)
                                .author(author)
                                .build();
                publishedAnnonce.setId(10L);

                when(annonceRepository.findById(10L)).thenReturn(Optional.of(draftAnnonce));
                when(annonceRepository.save(any(Annonce.class))).thenReturn(publishedAnnonce);

                PatchAnnonceDTO patchDTO = new PatchAnnonceDTO();
                patchDTO.setStatus("PUBLISHED");

                AnnonceDTO patchedDto = AnnonceDTO.builder().id(10L).title("Appart Lyon").status("PUBLISHED").build();
                when(annonceMapper.toDto(any(Annonce.class))).thenReturn(patchedDto);

                AnnonceDTO patched = annonceService.patch(10L, patchDTO, 1L);

                assertEquals("PUBLISHED", patched.getStatus());

                when(annonceRepository.findById(10L)).thenReturn(Optional.of(publishedAnnonce));

                AnnonceDTO found = annonceService.findById(10L);

                assertEquals(10L, found.getId());
                assertEquals("PUBLISHED", found.getStatus());
                assertEquals("Appart Lyon", found.getTitle());
        }
}