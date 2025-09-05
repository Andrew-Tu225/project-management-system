package com.aceproject.projectmanagementsystem.backend.user;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

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

    private Instant createdAt;
    private Instant lastLoginAt;
}
