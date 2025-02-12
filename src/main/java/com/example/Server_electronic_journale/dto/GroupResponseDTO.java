package com.example.Server_electronic_journale.dto;

import lombok.Data;
import java.util.List;

@Data
public class GroupResponseDTO {
    private int groupId;
    private String name;
    private List<SubjectResponseDTO> subjects;
}
