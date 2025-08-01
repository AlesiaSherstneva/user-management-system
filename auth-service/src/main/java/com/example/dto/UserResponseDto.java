package com.example.dto;

import com.example.entity.enums.Role;
import lombok.Data;

@Data
public class UserResponseDto {
    private String fullName;
    private String email;
    private Role role;
}