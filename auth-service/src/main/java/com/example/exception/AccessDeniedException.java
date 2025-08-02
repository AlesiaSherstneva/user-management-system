package com.example.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends BaseException {
    private static final HttpStatus STATUS = HttpStatus.FORBIDDEN;

    public AccessDeniedException() {
        super("Your access level doesn't allow this action", STATUS);
    }
}