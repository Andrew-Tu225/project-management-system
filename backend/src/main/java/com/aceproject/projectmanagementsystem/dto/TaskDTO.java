package com.aceproject.projectmanagementsystem.dto;

import com.aceproject.projectmanagementsystem.task.TaskImportance;
import com.aceproject.projectmanagementsystem.task.TaskProgress;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class TaskDTO {
    private int id;
    private String title;
    private String description;
    private TaskImportance importance;
    private TaskProgress progress;
    private TaskProjectDTO project;
    private List<UserDTO> people = new ArrayList<>();
    private Date startDate;
    private Date dueDate;
    private boolean completed;
}
