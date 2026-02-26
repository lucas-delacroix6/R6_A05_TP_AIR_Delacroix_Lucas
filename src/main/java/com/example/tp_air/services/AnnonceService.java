package com.example.tp_air.services;

import com.example.tp_air.dto.AnnonceDTO;
import com.example.tp_air.dto.PagedResponse;
import com.example.tp_air.dto.PatchAnnonceDTO;
import com.example.tp_air.models.Annonce;
import com.example.tp_air.models.AnnonceStatus;
import com.example.tp_air.models.Category;
import com.example.tp_air.models.User;
import com.example.tp_air.exceptions.BusinessException;
import com.example.tp_air.exceptions.ForbiddenException;
import com.example.tp_air.exceptions.NotFoundException;
import com.example.tp_air.mappers.AnnonceMapper;
import com.example.tp_air.repositories.AnnonceRepository;
import com.example.tp_air.repositories.CategoryRepository;
import com.example.tp_air.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class AnnonceService {

    private static final Logger log = LoggerFactory.getLogger(AnnonceService.class);

    private final AnnonceRepository annonceRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AnnonceMapper annonceMapper;

    public AnnonceService(AnnonceRepository annonceRepository, UserRepository userRepository,
            CategoryRepository categoryRepository, AnnonceMapper annonceMapper) {
        this.annonceRepository = annonceRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.annonceMapper = annonceMapper;
    }

    @Transactional(readOnly = true)
    public PagedResponse<AnnonceDTO> findAll(int page, int size) {
        Page<Annonce> annoncePage = annonceRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<AnnonceDTO> dtos = annoncePage.getContent().stream()
                .map(annonceMapper::toDto)
                .toList();

        return new PagedResponse<>(dtos, page, size, annoncePage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public AnnonceDTO findById(Long id) {
        return annonceRepository.findById(id)
                .map(annonceMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Annonce not found with id: " + id));
    }

    public AnnonceDTO create(AnnonceDTO dto, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + authorId));

        Category category = null;
        if (dto.getCategory() != null) {
            category = categoryRepository.findByLabel(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found with label: " + dto.getCategory()));
        }

        Annonce annonce = annonceMapper.toEntity(dto);
        annonce.setStatus(AnnonceStatus.DRAFT);
        annonce.setAuthor(author);
        annonce.setCategory(category);

        Annonce saved = annonceRepository.save(annonce);
        log.info("Created annonce id={} by user={}", saved.getId(), author.getUsername());
        return annonceMapper.toDto(saved);
    }

    public AnnonceDTO update(Long id, AnnonceDTO dto, Long currentUserId) {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Annonce not found with id: " + id));

        checkAuthor(annonce, currentUserId);
        checkNotPublished(annonce);

        annonceMapper.updateEntityFromDto(dto, annonce);

        if (dto.getCategory() != null) {
            Category category = categoryRepository.findByLabel(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found with label: " + dto.getCategory()));
            annonce.setCategory(category);
        }

        if (dto.getStatus() != null) {
            AnnonceStatus newStatus = parseStatus(dto.getStatus());
            validateStatusTransition(annonce.getStatus(), newStatus);
            if (newStatus == AnnonceStatus.ARCHIVED) {
                checkIsAdmin();
            }
            annonce.setStatus(newStatus);
        }

        Annonce updated = annonceRepository.save(annonce);
        log.info("Updated annonce id={} by user={}", id, currentUserId);
        return annonceMapper.toDto(updated);
    }

    public AnnonceDTO patch(Long id, PatchAnnonceDTO dto, Long currentUserId) {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Annonce not found with id: " + id));

        checkAuthor(annonce, currentUserId);
        checkNotPublished(annonce);

        annonceMapper.patchEntityFromDto(dto, annonce);

        if (dto.getCategory() != null) {
            Category category = categoryRepository.findByLabel(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found with label: " + dto.getCategory()));
            annonce.setCategory(category);
        }

        if (dto.getStatus() != null) {
            AnnonceStatus newStatus = parseStatus(dto.getStatus());
            validateStatusTransition(annonce.getStatus(), newStatus);
            if (newStatus == AnnonceStatus.ARCHIVED) {
                checkIsAdmin();
            }
            annonce.setStatus(newStatus);
        }

        Annonce updated = annonceRepository.save(annonce);
        log.info("Patched annonce id={} by user={}", id, currentUserId);
        return annonceMapper.toDto(updated);
    }

    public void delete(Long id, Long currentUserId) {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Annonce not found with id: " + id));

        checkAuthor(annonce, currentUserId);

        if (annonce.getStatus() != AnnonceStatus.ARCHIVED) {
            throw new BusinessException("Annonce must be ARCHIVED before deletion");
        }

        annonceRepository.delete(annonce);
        log.info("Deleted annonce id={} by user={}", id, currentUserId);
    }

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
        if (annonce.getStatus() == AnnonceStatus.PUBLISHED) {
            throw new BusinessException("A PUBLISHED annonce cannot be modified");
        }
    }

    private void checkIsAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new ForbiddenException("Authentication required");
        }
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        boolean isAdmin = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new ForbiddenException("Only ADMIN can archive annonces");
        }
    }

    private AnnonceStatus parseStatus(String statusStr) {
        try {
            return AnnonceStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid status: " + statusStr);
        }
    }

    private void validateStatusTransition(AnnonceStatus current, AnnonceStatus next) {
        if (current == AnnonceStatus.ARCHIVED) {
            throw new BusinessException("An ARCHIVED annonce cannot change status");
        }
        if (current == AnnonceStatus.PUBLISHED && next == AnnonceStatus.DRAFT) {
            throw new BusinessException("Cannot revert PUBLISHED annonce to DRAFT");
        }
    }
}