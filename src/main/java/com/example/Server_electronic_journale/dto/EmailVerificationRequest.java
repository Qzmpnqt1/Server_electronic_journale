package com.example.Server_electronic_journale.dto;

import lombok.Data;

@Data
public class EmailVerificationRequest {
    private String email;
    private String code;
}
