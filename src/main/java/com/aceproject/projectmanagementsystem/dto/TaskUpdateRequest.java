package com.aceproject.projectmanagementsystem.backend.dto;

import com.aceproject.projectmanagementsystem.backend.task.TaskImportance;
import com.aceproject.projectmanagementsystem.backend.task.TaskProgress;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class TaskUpdateRequest {
    private String title;
    private String description;
    private TaskImportance importance;
    private TaskProgress progress;
    private List<UserDTO> people = new ArrayList<>();
    private Date startDate;
    private Date dueDate;
}
