package com.aceproject.projectmanagementsystem.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskProjectDTO {
    private long id;
    private String name;
}
