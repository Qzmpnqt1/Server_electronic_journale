package com.example.Server_electronic_journale.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GradeEntryDTO {
    private int entryId;
    private String subjectName;

    private Integer winterGrade;
    private LocalDateTime winterDateAssigned;

    private Integer summerGrade;
    private LocalDateTime summerDateAssigned;
}
