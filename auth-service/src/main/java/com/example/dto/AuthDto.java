package com.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthDto {
    @NotBlank(message = "Email shouldn't be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password shouldn't be empty")
    @Size(min = 8, message = "Password should contain at least 8 symbols")
    private String password;
}