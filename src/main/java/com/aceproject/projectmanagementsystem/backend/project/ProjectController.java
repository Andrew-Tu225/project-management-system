package com.aceproject.projectmanagementsystem.backend.project;

import com.aceproject.projectmanagementsystem.backend.dto.CreateProjectRequest;
import com.aceproject.projectmanagementsystem.backend.dto.ProjectDTO;
import com.aceproject.projectmanagementsystem.backend.dto.UserDTO;
import com.aceproject.projectmanagementsystem.backend.user.UserExtractorService;
import com.aceproject.projectmanagementsystem.backend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;
    private final UserExtractorService userExtractor;

    @Autowired
    public  ProjectController(ProjectService projectService, UserService userService,  UserExtractorService userExtractor) {
        this.projectService = projectService;
        this.userService = userService;
        this.userExtractor = userExtractor;
    }

    @PostMapping("/create")
    public ResponseEntity<ProjectDTO> createProject(@RequestBody CreateProjectRequest createProjectRequest, Authentication authentication) {
        UserDTO userDTO = userExtractor.extractUser(authentication);
        try{
            ProjectDTO projectDTO = projectService.createProject(createProjectRequest, userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(projectDTO);
        }
        catch(Exception ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects(Authentication authentication){
        UserDTO userDTO = userExtractor.extractUser(authentication);
        try{
            List<ProjectDTO> projectDTOList = userService.getProjects(userDTO);
            return ResponseEntity.status(HttpStatus.OK).body(projectDTOList);
        }
        catch(Exception ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
