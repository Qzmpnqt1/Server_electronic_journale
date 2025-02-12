package com.example.Server_electronic_journale.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
