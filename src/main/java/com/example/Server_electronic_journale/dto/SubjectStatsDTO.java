package com.example.Server_electronic_journale.dto;

import lombok.Data;

@Data
public class SubjectStatsDTO {
    private String subjectName;

    // Зимняя сессия
    private String bestWinterStudent;
    private Integer bestWinterGrade;
    private Double averageWinterGrade;
    private String worstWinterStudent;
    private Integer worstWinterGrade;

    // Летняя сессия
    private String bestSummerStudent;
    private Integer bestSummerGrade;
    private Double averageSummerGrade;
    private String worstSummerStudent;
    private Integer worstSummerGrade;
}
