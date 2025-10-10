package com.aceproject.projectmanagementsystem.project;

import com.aceproject.projectmanagementsystem.dto.CreateProjectRequest;
import com.aceproject.projectmanagementsystem.dto.ProjectDTO;
import com.aceproject.projectmanagementsystem.dto.ProjectUpdateRequest;
import com.aceproject.projectmanagementsystem.dto.UserDTO;
import com.aceproject.projectmanagementsystem.exception.AuthorizationErrorException;
import com.aceproject.projectmanagementsystem.exception.ResourceNotFoundException;
import com.aceproject.projectmanagementsystem.user.UserExtractorService;
import com.aceproject.projectmanagementsystem.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
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

    /**
     * api to create the Project using CreateProjectRequest and user authentication
     * @param createProjectRequest: the request containing requiring fields for the Project
     * @param authentication: user who currently login(used to specify the creator of the project)
     * @return ResponseEntity contains:
     *                      - 201 CREATED with projectDTO if project is created successfully
     *                      - 404 NOT FOUND if user is not found in database(user with this authentication doesn't exist)
     */
    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestBody CreateProjectRequest createProjectRequest, Authentication authentication) {
        UserDTO userDTO = userExtractor.extractUser(authentication);
        try{
            ProjectDTO projectDTO = projectService.createProject(createProjectRequest, userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(projectDTO);
        }
        catch(ResourceNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    /**
     * Get all the projects of current user
     * @param authentication: user who currently login(user we try to get projects from)
     * @return ResponseEntity contains:
     *                      - 200 OK with list of projectDTO if the request is successfully
     *                      - 404 NOT FOUND if user cannot be found
     */
    @GetMapping
    public ResponseEntity<?> getAllProjects(Authentication authentication){
        UserDTO userDTO = userExtractor.extractUser(authentication);
        try{
            List<ProjectDTO> projectDTOList = userService.getProjects(userDTO);
            return ResponseEntity.status(HttpStatus.OK).body(projectDTOList);
        }
        catch(ResourceNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    /**
     * update the project with id specified
     * @param id: the id of the project
     * @param updateRequest: the request with updated fields about current project
     * @param authentication: user who currently login(used to check whether user is authorized for the update)
     * @return ResponseEntity contains:
     *                      - 200 OK with projectDTO of updated project if update is successful
     *                      - 401 Unauthorized if current user isn't collaborators of project
     *                      - 404 NOT FOUND if project id is not found
     */
    @PutMapping("update/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id, @RequestBody ProjectUpdateRequest updateRequest, Authentication authentication){
        UserDTO userDTO = userExtractor.extractUser(authentication);
        try{
            ProjectDTO projectDTO = projectService.updateProject(id, updateRequest, userDTO);
            return ResponseEntity.status(HttpStatus.OK).body(projectDTO);
        }
        catch(AuthorizationErrorException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
        catch(ResourceNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    /**
     * delete the project specified by id
     * @param id: id of the project user wants to delete
     * @param authentication: user who currently login(used to check whether user is authorized for deleting specific project)
     * @return ResponseEntity contains:
     *                      - 200 OK if project is deleted successfully
     *                      - 404 NOT FOUND if project is not found
     *                      - 401 Unauthorized if user is not one of collaborators in project
     */
    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id, Authentication authentication){
        UserDTO userDTO = userExtractor.extractUser(authentication);
        try{
            projectService.deleteProject(id, userDTO);
            return ResponseEntity.status(HttpStatus.OK).body("Project has been deleted");
        }
        catch(ResourceNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        catch(AuthorizationErrorException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }
}
