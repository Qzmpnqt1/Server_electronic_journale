package com.example.Server_electronic_journale.dto;

import lombok.Data;
import java.util.Set;

@Data
public class TeacherSignUpRequest {
    private String email;
    private String password;
    private String name;
    private String surname;
    private String patronymic;
    private Set<Integer> subjectIds; // ID предметов, которые ведет учитель
}
