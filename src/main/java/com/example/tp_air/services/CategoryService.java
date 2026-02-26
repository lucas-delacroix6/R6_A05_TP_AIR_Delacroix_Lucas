package com.example.tp_air.services;

import com.example.tp_air.dto.CategoryDTO;
import com.example.tp_air.exceptions.NotFoundException;
import com.example.tp_air.mappers.CategoryMapper;
import com.example.tp_air.models.Category;
import com.example.tp_air.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
        return categoryMapper.toDTO(category);
    }

    public CategoryDTO create(CategoryDTO categoryDTO) {
        if (categoryRepository.findByLabel(categoryDTO.getLabel()).isPresent()) {
            throw new IllegalArgumentException("Category with this label already exists");
        }
        Category category = categoryMapper.toEntity(categoryDTO);
        Category saved = categoryRepository.save(category);
        return categoryMapper.toDTO(saved);
    }

    public CategoryDTO update(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));

        category.setLabel(categoryDTO.getLabel());
        Category updated = categoryRepository.save(category);
        return categoryMapper.toDTO(updated);
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }
}
