package com.aceproject.projectmanagementsystem.Notification;

import com.aceproject.projectmanagementsystem.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name="notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private String receiverEmail;
    private String message;
    private Date sendAt = new Date();
    private boolean viewed = false;
}
