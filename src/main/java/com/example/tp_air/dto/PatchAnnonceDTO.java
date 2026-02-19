package com.example.tp_air.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO for partial updates (PATCH).
 * Only non-null fields will be applied.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatchAnnonceDTO {

    @Size(min = 3, max = 255, message = "title must be between 3 and 255 characters")
    private String title;

    private String description;

    @Positive(message = "price must be positive")
    private BigDecimal price;

    @Size(max = 100)
    private String category;

    /** Allowed values: DRAFT, PUBLISHED, ARCHIVED */
    private String status;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}