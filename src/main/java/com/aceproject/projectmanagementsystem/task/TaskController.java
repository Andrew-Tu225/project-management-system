package com.aceproject.projectmanagementsystem.backend.task;

import com.aceproject.projectmanagementsystem.backend.dto.TaskCreateDTO;
import com.aceproject.projectmanagementsystem.backend.dto.TaskDTO;
import com.aceproject.projectmanagementsystem.backend.dto.TaskUpdateRequest;
import com.aceproject.projectmanagementsystem.backend.dto.UserDTO;
import com.aceproject.projectmanagementsystem.backend.user.UserExtractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("tasks")
public class TaskController {
    private final TaskService taskService;
    private final UserExtractorService userExtractorService;

    @Autowired
    public TaskController(TaskService taskService, UserExtractorService userExtractorService) {
        this.taskService = taskService;
        this.userExtractorService = userExtractorService;
    }

    @GetMapping("/active-project-tasks")
    public ResponseEntity<List<TaskDTO>> getActiveProjectTasksByProgress(@RequestParam long projectId,
                                                             @RequestParam TaskProgress taskProgress) {
        List<TaskDTO> activeProjectTasksByProgress = taskService.getActiveProjectTasksByProgress(projectId, taskProgress);
        return new ResponseEntity<>(activeProjectTasksByProgress, HttpStatus.OK);
    }

    @GetMapping("/user-active-tasks")
    public ResponseEntity<List<TaskDTO>> getUserActiveTasksByProgress(@RequestParam TaskProgress taskProgress,
                                                                      Authentication authentication) {
        UserDTO user = userExtractorService.extractUser(authentication);
        List<TaskDTO> userActiveTasksWithProgress = taskService.getUserActiveTasksByProgress(user, taskProgress);
        return new ResponseEntity<>(userActiveTasksWithProgress, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTask(@RequestBody TaskCreateDTO taskCreateDTO, Authentication authentication) {
        try{
            UserDTO userDTO = userExtractorService.extractUser(authentication);
            TaskDTO newTask = taskService.createTask(taskCreateDTO, userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
        }
        catch(Exception ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/update/{taskId}")
    public ResponseEntity<TaskDTO> updateTask(@RequestBody TaskUpdateRequest taskUpdateRequest,
                                              @PathVariable long taskId) {
        try{
            TaskDTO updatedTask = taskService.updateTask(taskUpdateRequest, taskId);
            return ResponseEntity.ok(updatedTask);
        }
        catch(Exception ex){
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable long taskId) {
        try{
            taskService.deleteTask(taskId);
            return ResponseEntity.ok().body("task deleted successfully");
        }
        catch(Exception ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
