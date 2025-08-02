package com.example.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyRegisteredException extends BaseException {
    private static final HttpStatus STATUS = HttpStatus.CONFLICT;

    public EmailAlreadyRegisteredException(String email) {
        super(String.format("Email %s was already registered", email), STATUS);
    }
}