package com.example.tp_air.services;

import com.example.tp_air.dto.AnnonceDTO;
import com.example.tp_air.exceptions.BusinessException;
import com.example.tp_air.exceptions.ForbiddenException;
import com.example.tp_air.mappers.AnnonceMapper;
import com.example.tp_air.models.Annonce;
import com.example.tp_air.models.AnnonceStatus;
import com.example.tp_air.models.User;
import com.example.tp_air.repositories.AnnonceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnnonceServiceTest {

    @Mock
    private AnnonceRepository annonceRepository;

    @Mock
    private AnnonceMapper annonceMapper;

    @InjectMocks
    private AnnonceService annonceService;

    private Annonce annonce;
    private AnnonceDTO annonceDTO;

    @BeforeEach
    void setUp() {
        User authorUser = User.builder()
                .username("author")
                .password("pwd")
                .email("a@a.com")
                .role(User.Role.ROLE_USER)
                .build();
        authorUser.setId(1L);

        User otherUser = User.builder()
                .username("other")
                .password("pwd")
                .email("b@b.com")
                .role(User.Role.ROLE_USER)
                .build();
        otherUser.setId(2L);

        annonce = Annonce.builder()
                .title("Test Annonce")
                .description("Desc")
                .price(BigDecimal.TEN)
                .status(AnnonceStatus.DRAFT)
                .author(authorUser)
                .build();
        annonce.setId(100L);

        annonceDTO = AnnonceDTO.builder()
                .id(100L)
                .title("Test Annonce")
                .description("Desc")
                .price(BigDecimal.TEN)
                .status("DRAFT")
                .authorId(1L)
                .build();
    }

    @Test
    void testUpdate_Success() {
        when(annonceRepository.findById(100L)).thenReturn(Optional.of(annonce));
        doNothing().when(annonceMapper).updateEntityFromDto(any(), any());
        when(annonceRepository.save(any(Annonce.class))).thenReturn(annonce);
        when(annonceMapper.toDto(any(Annonce.class))).thenReturn(annonceDTO);

        AnnonceDTO result = annonceService.update(100L, annonceDTO, 1L);

        assertNotNull(result);
        verify(annonceRepository).save(annonce);
    }

    @Test
    void testUpdate_NotAuthor() {
        when(annonceRepository.findById(100L)).thenReturn(Optional.of(annonce));

        assertThrows(ForbiddenException.class, () -> annonceService.update(100L, annonceDTO, 2L));

        verify(annonceRepository, never()).save(any());
    }

    @Test
    void testUpdate_PublishedAnnonceCannotBeModified() {
        annonce.setStatus(AnnonceStatus.PUBLISHED);
        when(annonceRepository.findById(100L)).thenReturn(Optional.of(annonce));

        assertThrows(BusinessException.class, () -> annonceService.update(100L, annonceDTO, 1L));

        verify(annonceRepository, never()).save(any());
    }

    @Test
    void testArchive_RequiresAdminRole() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("author", "pwd",
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));

        annonceDTO.setStatus("ARCHIVED");
        when(annonceRepository.findById(100L)).thenReturn(Optional.of(annonce));

        assertThrows(ForbiddenException.class, () -> annonceService.update(100L, annonceDTO, 1L));
    }

    @Test
    void testArchive_WithAdminRole() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", "pwd",
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        annonceDTO.setStatus("ARCHIVED");
        when(annonceRepository.findById(100L)).thenReturn(Optional.of(annonce));
        when(annonceRepository.save(any(Annonce.class))).thenReturn(annonce);
        when(annonceMapper.toDto(any(Annonce.class))).thenReturn(annonceDTO);

        assertDoesNotThrow(() -> {
            annonceService.update(100L, annonceDTO, 1L);
        });
        assertEquals(AnnonceStatus.ARCHIVED, annonce.getStatus());
    }
}