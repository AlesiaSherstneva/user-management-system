package com.example.service.mapper;

import com.example.dto.UserCreateDto;
import com.example.dto.UserResponseDto;
import com.example.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponseDto userToResponseDto(User user) {
        return UserResponseDto.builder()
                .fullName(String.format("%s %s", user.getFirstName(), user.getLastName()))
                .email(user.getEmail())
                .build();
    }

    public User createDtoToUser(UserCreateDto dto) {
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .build();
    }
}