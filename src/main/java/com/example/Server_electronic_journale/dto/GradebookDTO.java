package com.example.Server_electronic_journale.dto;

import lombok.Data;

import java.util.List;

@Data
public class GradebookDTO {
    private int gradebookId;
    private int studentId;
    private List<GradeEntryDTO> gradeEntries;
}
