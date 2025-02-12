package com.example.Server_electronic_journale.dto;

import lombok.Data;

@Data
public class GradeEntryRequest {
    private int studentId;
    private int subjectId;
    private int grade;
}

