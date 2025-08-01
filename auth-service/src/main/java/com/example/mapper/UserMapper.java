package com.example.mapper;

import com.example.dto.UserResponseDto;
import com.example.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "fullName",
            expression = "java(String.format(\"%s %s\", user.getFirstName(), user.getLastName()))")
    UserResponseDto userToResponseDto(User user);
}