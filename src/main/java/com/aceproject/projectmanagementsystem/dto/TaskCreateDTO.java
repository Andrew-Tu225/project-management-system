package com.aceproject.projectmanagementsystem.backend.dto;

import com.aceproject.projectmanagementsystem.backend.task.TaskImportance;
import com.aceproject.projectmanagementsystem.backend.task.TaskProgress;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class TaskCreateDTO {
    private String title;
    private String description;
    private long projectId;
    private TaskImportance importance = TaskImportance.NONE;
    private TaskProgress progress = TaskProgress.NOT_STARTED;
    private List<UserDTO> people = new ArrayList<>();
    private Date startDate;
    private Date dueDate;
}
