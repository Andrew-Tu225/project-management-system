package com.easya.projectmanagementsystem.backend.dto;

import com.easya.projectmanagementsystem.backend.semesters.Semester;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class UserDTO {

    private String username;

    private List<Semester> semesters;

    public UserDTO(@NonNull String username) {
        this.username = username;
    }

    public UserDTO(@NonNull String username, List<Semester> semesters) {
        this.username = username;
        this.semesters = semesters;
    }
}
