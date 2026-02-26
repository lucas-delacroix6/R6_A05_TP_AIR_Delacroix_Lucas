package com.example.tp_air.mappers;

import com.example.tp_air.dto.CategoryDTO;
import com.example.tp_air.models.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDTO(Category entity);

    Category toEntity(CategoryDTO dto);
}
