package com.example.tp_air.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnnonceDTO {
    private Long id;

    @NotBlank(message = "title is required")
    @Size(min = 3, max = 255, message = "title must be between 3 and 255 characters")
    private String title;

    @NotBlank(message = "description is required")
    private String description;

    @Positive(message = "price must be positive")
    private BigDecimal price;

    @Size(max = 100, message = "category must be at most 100 characters")
    private String category;

    private String status;

    private String authorUsername;
    private Long authorId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final AnnonceDTO dto = new AnnonceDTO();

        public Builder id(Long id) {
            dto.id = id;
            return this;
        }

        public Builder title(String title) {
            dto.title = title;
            return this;
        }

        public Builder description(String description) {
            dto.description = description;
            return this;
        }

        public Builder price(BigDecimal price) {
            dto.price = price;
            return this;
        }

        public Builder category(String category) {
            dto.category = category;
            return this;
        }

        public Builder status(String status) {
            dto.status = status;
            return this;
        }

        public Builder authorUsername(String authorUsername) {
            dto.authorUsername = authorUsername;
            return this;
        }

        public Builder authorId(Long authorId) {
            dto.authorId = authorId;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            dto.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            dto.updatedAt = updatedAt;
            return this;
        }

        public AnnonceDTO build() {
            return dto;
        }
    }
}