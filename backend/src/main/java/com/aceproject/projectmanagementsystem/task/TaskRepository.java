package com.aceproject.projectmanagementsystem.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
    @Query("SELECT t FROM Task t where t.project.id = :projectId AND t.dueDate >= CURRENT_DATE")
    List<Task> findByProjectBeforeDueDate(@Param("projectId") long projectId);

    @Query("SELECT t from Task t JOIN t.people p WHERE p.email = :userEmail AND t.dueDate >= CURRENT_DATE")
    List<Task> findTasksByPersonBeforeDueDate(@Param("userEmail") String userEmail);
}
