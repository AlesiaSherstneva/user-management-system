package com.example.service.impl;

import com.example.dto.UserResponseDto;
import com.example.mapper.UserMapper;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto getUserById(Long id) {
        return userMapper.userToResponseDto(userRepository.findUserById(id));
    }
}