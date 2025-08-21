package com.easya.projectmanagementsystem.backend.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SemesterDTO {
    private long id;
    private String name;
    private Date startDate;
    private Date endDate;
    private UserDTO user;
}
