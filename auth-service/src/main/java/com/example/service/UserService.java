package com.example.service;

import com.example.dto.AuthDto;
import com.example.dto.UserUpdateDto;
import com.example.model.User;

public interface UserService {
    User getUserById(Long id);

    User createUser(User newUser, String rawPassword);

    User updateUser(Long id, UserUpdateDto userUpdateDto);

    User authenticateUser(AuthDto authDto);

    void deleteUser(Long userId);
}