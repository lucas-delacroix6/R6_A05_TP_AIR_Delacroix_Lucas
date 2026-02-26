package com.example.tp_air.mappers;

import com.example.tp_air.dto.UserCreateDTO;
import com.example.tp_air.dto.UserDTO;
import com.example.tp_air.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface UserMapper {
    UserDTO toDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    User toEntity(UserCreateDTO dto);
}
