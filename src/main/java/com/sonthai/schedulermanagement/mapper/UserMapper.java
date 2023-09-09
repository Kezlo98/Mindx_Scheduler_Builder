package com.sonthai.schedulermanagement.mapper;

import com.sonthai.schedulermanagement.entity.User;
import com.sonthai.schedulermanagement.model.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password",ignore = true)
    UserDto toDto(User user);

    @Mapping(target = "password",ignore = true)
    List<UserDto> toDto(List<User> user);

    User toEntity(UserDto userDto);
}
