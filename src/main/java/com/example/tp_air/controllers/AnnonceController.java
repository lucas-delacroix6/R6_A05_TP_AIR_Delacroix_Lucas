package com.example.tp_air.controllers;

import com.example.tp_air.dto.AnnonceDTO;
import com.example.tp_air.dto.PagedResponse;
import com.example.tp_air.dto.PatchAnnonceDTO;
import com.example.tp_air.security.CustomUserDetails;
import com.example.tp_air.services.AnnonceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/annonces")
@Tag(name = "Annonces", description = "CRUD operations on Annonces")
public class AnnonceController {

    private final AnnonceService annonceService;

    public AnnonceController(AnnonceService annonceService) {
        this.annonceService = annonceService;
    }

    @GetMapping
    @Operation(summary = "List annonces (paginated)")
    public ResponseEntity<PagedResponse<AnnonceDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (size > 100)
            size = 100;
        PagedResponse<AnnonceDTO> result = annonceService.findAll(page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get annonce by ID")
    public ResponseEntity<AnnonceDTO> getById(@PathVariable Long id) {
        AnnonceDTO dto = annonceService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Create annonce", security = @SecurityRequirement(name = "BearerAuth"))
    public ResponseEntity<AnnonceDTO> create(@Valid @RequestBody AnnonceDTO dto) {
        Long userId = getCurrentUserId();
        AnnonceDTO created = annonceService.create(dto, userId);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Update annonce (full)", security = @SecurityRequirement(name = "BearerAuth"))
    public ResponseEntity<AnnonceDTO> update(@PathVariable Long id, @Valid @RequestBody AnnonceDTO dto) {
        Long userId = getCurrentUserId();
        AnnonceDTO updated = annonceService.update(id, dto, userId);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Partially update annonce", security = @SecurityRequirement(name = "BearerAuth"))
    public ResponseEntity<AnnonceDTO> patch(@PathVariable Long id, @Valid @RequestBody PatchAnnonceDTO dto) {
        Long userId = getCurrentUserId();
        AnnonceDTO updated = annonceService.patch(id, dto, userId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Delete annonce (must be ARCHIVED first)", security = @SecurityRequirement(name = "BearerAuth"))
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        annonceService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getId();
        }
        return null;
    }
}
