package com.example.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {
    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    public UserNotFoundException(Long userId) {
        super(String.format("User with id %s not found", userId), STATUS);
    }

    public UserNotFoundException(String email) {
        super(String.format("User with email %s not found", email), STATUS);
    }
}