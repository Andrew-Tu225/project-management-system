package com.aceproject.projectmanagementsystem.backend.dto;

import com.aceproject.projectmanagementsystem.backend.user.User;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProjectDTO {
    private long id;
    private String name;
    private String description;
    private List<UserDTO> collaborators;
    private List<String> links;
    private Date creationDate;
    private Date expectedFinishDate;
}
