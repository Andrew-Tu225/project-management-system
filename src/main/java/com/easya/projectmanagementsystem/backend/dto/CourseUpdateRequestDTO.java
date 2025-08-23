package com.easya.projectmanagementsystem.backend.dto;

import lombok.Data;

@Data
public class CourseUpdateRequestDTO {
    private CourseDTO course;
    private String username;
}
