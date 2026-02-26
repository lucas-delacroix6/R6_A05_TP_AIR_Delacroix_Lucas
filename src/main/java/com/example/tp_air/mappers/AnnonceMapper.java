package com.example.tp_air.mappers;

import com.example.tp_air.dto.AnnonceDTO;
import com.example.tp_air.dto.PatchAnnonceDTO;
import com.example.tp_air.models.Annonce;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface AnnonceMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorUsername", source = "author.username")
    @Mapping(target = "category", source = "category.label")
    AnnonceDTO toDto(Annonce annonce);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    Annonce toEntity(AnnonceDTO annonceDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntityFromDto(AnnonceDTO dto, @MappingTarget Annonce entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    void patchEntityFromDto(PatchAnnonceDTO patchDto, @MappingTarget Annonce entity);
}
