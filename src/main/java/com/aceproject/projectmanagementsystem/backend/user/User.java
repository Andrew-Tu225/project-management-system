package com.aceproject.projectmanagementsystem.backend.user;

import com.aceproject.projectmanagementsystem.backend.project.Project;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String provider;
    private String providerUserId;
    private String email;
    private String name;
    private String avatarUrl;

    @ManyToMany(mappedBy = "collaborators")
    private List<Project> projects = new ArrayList<>();

    private Instant createdAt;
    private Instant lastLoginAt;
}
