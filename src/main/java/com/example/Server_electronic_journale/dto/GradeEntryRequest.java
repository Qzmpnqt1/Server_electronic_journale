package com.example.Server_electronic_journale.dto;

import lombok.Data;

@Data
public class GradeEntryRequest {
    private int studentId;
    private int subjectId;
    private int grade; // пользователь отправляет: "Ставлю 4" — сервер сам поймёт, зимняя или летняя
}



