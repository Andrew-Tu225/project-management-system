package com.aceproject.projectmanagementsystem.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProjectUpdateRequest {
    private String name;
    private String description;
    private List<String> links;
    private Date expectedFinishDate;
}
