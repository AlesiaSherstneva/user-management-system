package com.example.controller;

import com.example.dto.AuthDto;
import com.example.dto.TokenTransferDto;
import com.example.dto.UserCreateDto;
import com.example.model.User;
import com.example.security.JwtProvider;
import com.example.service.UserService;
import com.example.service.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<TokenTransferDto> login(@RequestBody AuthDto authDto) {
        log.info("Authentication attempt for email: {}", authDto.getEmail());
        User user = userService.authenticateUser(authDto);

        return ResponseEntity.ok(
                TokenTransferDto.builder()
                        .bearerToken(jwtProvider.generateToken(user))
                        .role(user.getRole())
                        .build()
        );
    }

    @PostMapping("/register")
    public ResponseEntity<TokenTransferDto> register(@Valid @RequestBody UserCreateDto userCreateDto) {
        log.info("Registration started for email: {}", userCreateDto.getEmail());
        User createdUser = userService.createUser(
                userMapper.createDtoToUser(userCreateDto),
                userCreateDto.getPassword()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TokenTransferDto.builder()
                        .bearerToken(jwtProvider.generateToken(createdUser))
                        .role(createdUser.getRole())
                        .build());
    }
}