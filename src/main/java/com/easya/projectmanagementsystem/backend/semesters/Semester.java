package com.easya.projectmanagementsystem.backend.semesters;

import com.easya.projectmanagementsystem.backend.User.User;
import com.easya.projectmanagementsystem.backend.courses.Course;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Table(name="semesters")
@Data
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;
    private Date startDate;
    private Date endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "semester")
    private List<Course> courses;
}
