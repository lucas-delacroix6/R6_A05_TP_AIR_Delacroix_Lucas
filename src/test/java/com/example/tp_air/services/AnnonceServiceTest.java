package com.example.tp_air.services;

import com.example.tp_air.dto.AnnonceDTO;
import com.example.tp_air.models.Annonce;
import com.example.tp_air.models.User;
import com.example.tp_air.exceptions.BusinessException;
import com.example.tp_air.exceptions.ForbiddenException;
import com.example.tp_air.exceptions.NotFoundException;
import com.example.tp_air.repositories.AnnonceRepository;
import com.example.tp_air.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AnnonceService using Mockito.
 */
@ExtendWith(MockitoExtension.class)
class AnnonceServiceTest {

    @Mock
    private AnnonceRepository annonceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AnnonceService annonceService;

    private User author;
    private Annonce draftAnnonce;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);
        author.setUsername("testuser");
        author.setRole(User.Role.ROLE_USER);

        draftAnnonce = Annonce.builder()
                .title("Test Annonce")
                .description("Description")
                .price(new BigDecimal("100.00"))
                .status(Annonce.Status.DRAFT)
                .author(author)
                .build();
        // Simulate persisted ID
        try {
            var idField = Annonce.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(draftAnnonce, 1L);
        } catch (Exception ignored) {}
    }

    @Test
    void findById_existingId_returnsDTO() {
        when(annonceRepository.findById(1L)).thenReturn(Optional.of(draftAnnonce));

        AnnonceDTO result = annonceService.findById(1L);

        assertNotNull(result);
        assertEquals("Test Annonce", result.getTitle());
    }

    @Test
    void findById_unknownId_throwsNotFoundException() {
        when(annonceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> annonceService.findById(99L));
    }

    @Test
    void delete_notArchived_throwsBusinessException() {
        when(annonceRepository.findById(1L)).thenReturn(Optional.of(draftAnnonce));

        assertThrows(BusinessException.class, () -> annonceService.delete(1L, 1L));
        verify(annonceRepository, never()).delete(anyLong());
    }

    @Test
    void delete_notAuthor_throwsForbiddenException() {
        when(annonceRepository.findById(1L)).thenReturn(Optional.of(draftAnnonce));

        // currentUserId = 99 != author.id = 1
        assertThrows(ForbiddenException.class, () -> annonceService.delete(1L, 99L));
    }

    @Test
    void update_publishedAnnonce_throwsBusinessException() {
        Annonce published = Annonce.builder()
                .title("Published")
                .description("Desc")
                .status(Annonce.Status.PUBLISHED)
                .author(author)
                .build();
        try {
            var idField = Annonce.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(published, 2L);
        } catch (Exception ignored) {}

        when(annonceRepository.findById(2L)).thenReturn(Optional.of(published));

        AnnonceDTO dto = new AnnonceDTO();
        dto.setTitle("New title");
        dto.setDescription("New description");

        assertThrows(BusinessException.class, () -> annonceService.update(2L, dto, 1L));
    }

    @Test
    void create_validData_returnsCreatedDTO() {
        AnnonceDTO dto = AnnonceDTO.builder()
                .title("New Annonce")
                .description("New Description")
                .price(new BigDecimal("150.00"))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(annonceRepository.save(any(Annonce.class))).thenAnswer(inv -> {
            Annonce a = inv.getArgument(0);
            try {
                var f = Annonce.class.getDeclaredField("id");
                f.setAccessible(true);
                f.set(a, 10L);
            } catch (Exception ignored) {}
            return a;
        });

        AnnonceDTO result = annonceService.create(dto, 1L);

        assertNotNull(result.getId());
        assertEquals("New Annonce", result.getTitle());
        assertEquals("DRAFT", result.getStatus());
    }
}