package com.example.Server_electronic_journale.dto;

import lombok.Data;
import java.util.List;

@Data
public class GroupDTO {
    private String name;
    private List<Integer> subjectIds;
}
