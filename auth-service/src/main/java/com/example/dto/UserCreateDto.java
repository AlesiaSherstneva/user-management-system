package com.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateDto {
    @NotBlank(message = "First name shouldn't be empty")
    @Size(min = 2, message = "First name must be at least 2 characters")
    private String firstName;

    @NotBlank(message = "Last name shouldn't be empty")
    @Size(min = 2, message = "Last name must be at least 2 characters")
    private String lastName;

    @NotBlank(message = "Email shouldn't be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password shouldn't be empty")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}