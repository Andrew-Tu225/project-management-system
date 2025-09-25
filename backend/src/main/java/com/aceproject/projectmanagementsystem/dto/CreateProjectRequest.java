package com.aceproject.projectmanagementsystem.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CreateProjectRequest {
    private String projectName;
    private String projectDescription;
    private Date creationDate;
    private Date expectedFinishDate;
}
