package com.example.dto;

import com.example.model.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenTransferDto {
    private Role role;
    private String bearerToken;
}