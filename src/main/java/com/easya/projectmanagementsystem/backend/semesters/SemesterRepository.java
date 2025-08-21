package com.easya.projectmanagementsystem.backend.semesters;

import com.easya.projectmanagementsystem.backend.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    Optional<Semester> findSemesterById(long id);
    Optional<Semester> findByName(String name);

    List<Semester> findByUserUsername(String username);

    @Query("SELECT s FROM Semester s WHERE s.user.username = :username ORDER BY s.startDate DESC")
    List<Semester> sortSemesterWithLatest(@Param("username")String username);

    @Query("SELECT s FROM Semester s WHERE s.user.username = :username ORDER BY s.startDate ASC")
    List<Semester> sortSemesterWithOldest(@Param("username")String username);

}
