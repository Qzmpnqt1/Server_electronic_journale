package com.example.Server_electronic_journale.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GradeEntryDTO {
    private int entryId;
    private String subjectName;

    private Integer winterGrade;
    private LocalDate winterDateAssigned;

    private Integer summerGrade;
    private LocalDate summerDateAssigned;
}
