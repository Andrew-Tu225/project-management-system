package com.easya.projectmanagementsystem.backend.courses;

import com.easya.projectmanagementsystem.backend.dto.CourseDTO;
import com.easya.projectmanagementsystem.backend.dto.CourseUpdateRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("course")
public class CourseController {
    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourse(@RequestBody String username, @PathVariable long id) {
        if(courseService.isUserCourseOwner(username, id)) {
            Course course = courseService.findById(id);
            CourseDTO courseDTO = courseService.convertToDTO(course);
            return new ResponseEntity<>(courseDTO, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCourse(@RequestBody CourseDTO courseDTO) {
        try{
            courseService.create(courseDTO);
            return  new ResponseEntity<>(HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{courseId}/update")
    public ResponseEntity<?> updateCourse(@RequestBody CourseUpdateRequestDTO courseUpdateRequestDTO, @PathVariable long courseId) {
        if(courseService.isUserCourseOwner(courseUpdateRequestDTO.getUsername(), courseId)) {
            courseService.update(courseUpdateRequestDTO.getCourse());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/{courseId}/delete")
    public ResponseEntity<?> deleteCourse(@RequestBody String username, @PathVariable long courseId) {
        if(courseService.isUserCourseOwner(username, courseId)) {
            courseService.delete(courseId);
            return new ResponseEntity<>("course is deleted successfully",HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
