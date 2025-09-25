package com.aceproject.projectmanagementsystem.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProjectUpdateRequest {
    private String name;
    private String description;
    private List<String> links;
}
