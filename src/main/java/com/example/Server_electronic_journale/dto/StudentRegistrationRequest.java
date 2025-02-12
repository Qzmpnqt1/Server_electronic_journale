package com.example.Server_electronic_journale.dto;

import lombok.Data;

@Data
public class StudentRegistrationRequest {
    private String name;
    private String surname;
    private String patronymic;
    private String dateOfBirth; // Формат: "yyyy-MM-dd"
    private String email;
    private String password;
    private int groupId;
}