package com.example.Server_electronic_journale.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentResponseDTO {
    private int studentId;
    private String name;
    private String surname;
    private String patronymic;
    private LocalDate dateOfBirth;
    private String email;
    private String role;
}
