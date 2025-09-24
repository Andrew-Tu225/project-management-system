package com.aceproject.projectmanagementsystem.backend.task;

import com.aceproject.projectmanagementsystem.backend.project.Project;
import com.aceproject.projectmanagementsystem.backend.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Data
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;
    private String description;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Enumerated(EnumType.STRING)
    private TaskImportance importance;

    @Enumerated(EnumType.STRING)
    private TaskProgress progress;

    @ManyToMany
    @JoinTable(
            name = "user_task",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> people =  new HashSet<>();

    private Date startDate;
    private Date dueDate;

    private boolean completed = false;

}
