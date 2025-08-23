package com.easya.projectmanagementsystem.backend.courses;

import com.easya.projectmanagementsystem.backend.semesters.Semester;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="courses")
@Data
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String courseName;

    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester;
}
