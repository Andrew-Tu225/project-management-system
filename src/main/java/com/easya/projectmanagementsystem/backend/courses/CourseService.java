package com.easya.projectmanagementsystem.backend.courses;

import com.easya.projectmanagementsystem.backend.User.User;
import com.easya.projectmanagementsystem.backend.dto.CourseDTO;
import com.easya.projectmanagementsystem.backend.semesters.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseService {
    private final CourseRepository courseRepo;
    private final SemesterRepository semesterRepo;

    @Autowired
    public CourseService(CourseRepository courseRepo,  SemesterRepository semesterRepo) {
        this.courseRepo = courseRepo;
        this.semesterRepo = semesterRepo;
    }

    public CourseDTO convertToDTO(Course course){
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setId(course.getId());
        courseDTO.setCourseName(course.getCourseName());
        courseDTO.setSemesterId(course.getSemester().getId());
        return courseDTO;
    }

    public boolean isUserCourseOwner(String username, long courseId) {
        Course course = courseRepo.findById(courseId).get();
        User courseOwner = course.getSemester().getUser();
        return username.equals(courseOwner.getUsername());
    }
    public Course findById(Long id) {
        return courseRepo.findById(id).orElse(null);
    }

    public void create(CourseDTO courseDTO) {
        Course newCourse = new Course();
        newCourse.setCourseName(courseDTO.getCourseName());
        if(semesterRepo.findById(courseDTO.getSemesterId()).isPresent()) {
            newCourse.setSemester(semesterRepo.findById(courseDTO.getSemesterId()).get());
            courseRepo.save(newCourse);
        }
        else{
            throw new RuntimeException("Semester not found");
        }
    }

    public void update(CourseDTO courseDTO) {
        Course course = findById(courseDTO.getId());
        if(course != null){
            course.setCourseName(courseDTO.getCourseName());
            courseRepo.save(course);
        }
        else{
            throw new RuntimeException("Course not found");
        }
    }

    public void delete(long id) {
        Course course = findById(id);
        courseRepo.delete(course);
    }

}
