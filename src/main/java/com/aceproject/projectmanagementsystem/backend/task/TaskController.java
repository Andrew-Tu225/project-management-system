package com.aceproject.projectmanagementsystem.backend.task;

import com.aceproject.projectmanagementsystem.backend.dto.TaskCreateDTO;
import com.aceproject.projectmanagementsystem.backend.dto.TaskDTO;
import com.aceproject.projectmanagementsystem.backend.dto.UserDTO;
import com.aceproject.projectmanagementsystem.backend.user.User;
import com.aceproject.projectmanagementsystem.backend.user.UserExtractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
