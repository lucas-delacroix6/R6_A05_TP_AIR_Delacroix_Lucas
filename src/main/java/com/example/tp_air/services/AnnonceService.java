package com.example.tp_air.services;

import com.example.tp_air.dto.AnnonceDTO;
import com.example.tp_air.dto.PagedResponse;
import com.example.tp_air.dto.PatchAnnonceDTO;
import com.example.tp_air.models.Annonce;
import com.example.tp_air.models.User;
import com.example.tp_air.exceptions.BusinessException;
import com.example.tp_air.exceptions.ForbiddenException;
import com.example.tp_air.exceptions.NotFoundException;
import com.example.tp_air.repositories.AnnonceRepository;
import com.example.tp_air.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class AnnonceService {

    private static final Logger log = LoggerFactory.getLogger(AnnonceService.class);

    private final AnnonceRepository annonceRepository;
    private final UserRepository userRepository;

    public AnnonceService() {
        this.annonceRepository = new AnnonceRepository();
        this.userRepository = new UserRepository();
    }

    // Constructor for testing (injection)
    public AnnonceService(AnnonceRepository annonceRepository, UserRepository userRepository) {
        this.annonceRepository = annonceRepository;
        this.userRepository = userRepository;
    }

    public PagedResponse<AnnonceDTO> findAll(int page, int size) {
        List<AnnonceDTO> dtos = annonceRepository.findAll(page, size)
                .stream().map(AnnonceDTO::fromEntity).collect(Collectors.toList());
        long total = annonceRepository.countAll();
        return new PagedResponse<>(dtos, page, size, total);
    }

    public AnnonceDTO findById(Long id) {
        return annonceRepository.findById(id)
                .map(AnnonceDTO::fromEntity)
                .orElseThrow(() -> new NotFoundException("Annonce not found with id: " + id));
    }

    public AnnonceDTO create(AnnonceDTO dto, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + authorId));

        Annonce annonce = Annonce.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .status(Annonce.Status.DRAFT)
                .author(author)
                .build();

        Annonce saved = annonceRepository.save(annonce);
        log.info("Created annonce id={} by user={}", saved.getId(), author.getUsername());
        return AnnonceDTO.fromEntity(saved);
    }

    public AnnonceDTO update(Long id, AnnonceDTO dto, Long currentUserId) {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Annonce not found with id: " + id));

        checkAuthor(annonce, currentUserId);
        checkNotPublished(annonce);

        annonce.setTitle(dto.getTitle());
        annonce.setDescription(dto.getDescription());
        annonce.setPrice(dto.getPrice());
        annonce.setCategory(dto.getCategory());

        if (dto.getStatus() != null) {
            Annonce.Status newStatus = parseStatus(dto.getStatus());
            validateStatusTransition(annonce.getStatus(), newStatus);
            annonce.setStatus(newStatus);
        }

        Annonce updated = annonceRepository.update(annonce);
        log.info("Updated annonce id={} by user={}", id, currentUserId);
        return AnnonceDTO.fromEntity(updated);
    }

    public AnnonceDTO patch(Long id, PatchAnnonceDTO dto, Long currentUserId) {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Annonce not found with id: " + id));

        checkAuthor(annonce, currentUserId);
        checkNotPublished(annonce);

        if (dto.getTitle() != null)
            annonce.setTitle(dto.getTitle());
        if (dto.getDescription() != null)
            annonce.setDescription(dto.getDescription());
        if (dto.getPrice() != null)
            annonce.setPrice(dto.getPrice());
        if (dto.getCategory() != null)
            annonce.setCategory(dto.getCategory());
        if (dto.getStatus() != null) {
            Annonce.Status newStatus = parseStatus(dto.getStatus());
            validateStatusTransition(annonce.getStatus(), newStatus);
            annonce.setStatus(newStatus);
        }

        Annonce updated = annonceRepository.update(annonce);
        log.info("Patched annonce id={} by user={}", id, currentUserId);
        return AnnonceDTO.fromEntity(updated);
    }

    public void delete(Long id, Long currentUserId) {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Annonce not found with id: " + id));

        checkAuthor(annonce, currentUserId);

        // Business rule: must be ARCHIVED before deletion
        if (annonce.getStatus() != Annonce.Status.ARCHIVED) {
            throw new BusinessException("Annonce must be ARCHIVED before deletion");
        }

        annonceRepository.delete(id);
        log.info("Deleted annonce id={} by user={}", id, currentUserId);
    }

    // --- Business rules ---

    public boolean isAuthor(Long annonceId, User user) {
        Annonce annonce = annonceRepository.findById(annonceId)
                .orElseThrow(() -> new NotFoundException("Annonce not found with id: " + annonceId));
        return annonce.getAuthor().getId().equals(user.getId());
    }

    private void checkAuthor(Annonce annonce, Long currentUserId) {
        if (!annonce.getAuthor().getId().equals(currentUserId)) {
            throw new ForbiddenException("You are not the author of this annonce");
        }
    }

    private void checkNotPublished(Annonce annonce) {
        if (annonce.getStatus() == Annonce.Status.PUBLISHED) {
            throw new BusinessException("A PUBLISHED annonce cannot be modified");
        }
    }

    private Annonce.Status parseStatus(String statusStr) {
        try {
            return Annonce.Status.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid status: " + statusStr);
        }
    }

    private void validateStatusTransition(Annonce.Status current, Annonce.Status next) {
        // DRAFT -> PUBLISHED or ARCHIVED
        // PUBLISHED -> ARCHIVED only
        // ARCHIVED -> nothing
        if (current == Annonce.Status.ARCHIVED) {
            throw new BusinessException("An ARCHIVED annonce cannot change status");
        }
        if (current == Annonce.Status.PUBLISHED && next == Annonce.Status.DRAFT) {
            throw new BusinessException("Cannot revert PUBLISHED annonce to DRAFT");
        }
    }
}