package ru.ob11to.springjwt.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.ob11to.springjwt.dto.UserCreateDto;
import ru.ob11to.springjwt.dto.UserReadDto;
import ru.ob11to.springjwt.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {


    @Mappings({
            @Mapping(target = "userRole.id", source = "userRoleId"),
            @Mapping(target = "login", source = "login"),
            @Mapping(target = "password", source = "password"),
            @Mapping(target = "name", source = "name"),
    })
    User toEntity(UserCreateDto dto);


    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "userRoleName", source = "userRole.name"),
            @Mapping(target = "login", source = "login"),
            @Mapping(target = "name", source = "name"),
    })
    UserReadDto toDto(User bracelet);
}
