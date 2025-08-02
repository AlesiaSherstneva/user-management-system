package com.example.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BaseException {
    private static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;

    public InvalidCredentialsException() {
        super("Invalid username or password", STATUS);
    }
}