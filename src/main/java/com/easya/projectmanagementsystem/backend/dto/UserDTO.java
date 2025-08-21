package com.easya.projectmanagementsystem.backend.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class UserDTO {

    private String username;

    public UserDTO(@NonNull String username) {
        this.username = username;
    }
}
