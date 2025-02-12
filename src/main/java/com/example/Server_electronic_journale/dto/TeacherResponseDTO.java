package com.example.Server_electronic_journale.dto;

import lombok.Data;

import java.util.Set;

@Data
public class TeacherResponseDTO {
    private int teacherId;
    private String name;
    private String surname;
    private String patronymic;
    private String email;
    private String role;
    private Set<SubjectResponseDTO> subjects;
}
