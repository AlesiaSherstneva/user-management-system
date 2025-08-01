package com.example.service;

import com.example.dto.UserResponseDto;

public interface UserService {
    UserResponseDto getUserById(Long id);
}