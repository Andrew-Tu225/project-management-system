package com.easya.projectmanagementsystem.backend.dto;

import lombok.Data;

@Data
public class CourseDTO {
    private long id;
    private String courseName;
    private long semesterId;
}
