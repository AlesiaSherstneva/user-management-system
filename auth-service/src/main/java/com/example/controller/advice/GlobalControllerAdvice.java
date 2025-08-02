package com.example.controller.advice;

import com.example.dto.ErrorDto;
import com.example.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorDto> handleException(BaseException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(buildResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorDto> handleException(BindException ex) {
        ErrorDto error = buildResponseDto("Incorrect fields");
        error.setDetails(ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage)));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    private ErrorDto buildResponseDto(String errorMessage) {
        return ErrorDto.builder()
                .message(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
    }
}