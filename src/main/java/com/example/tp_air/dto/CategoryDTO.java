package com.example.tp_air.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryDTO {
    private Long id;

    @NotBlank(message = "Label is mandatory")
    private String label;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
