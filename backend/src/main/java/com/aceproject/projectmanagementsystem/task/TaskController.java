package com.aceproject.projectmanagementsystem.task;

import com.aceproject.projectmanagementsystem.dto.TaskCreateDTO;
import com.aceproject.projectmanagementsystem.dto.TaskDTO;
import com.aceproject.projectmanagementsystem.dto.TaskUpdateRequest;
import com.aceproject.projectmanagementsystem.dto.UserDTO;
import com.aceproject.projectmanagementsystem.user.UserExtractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/tasks")
public class TaskController {
    private final TaskService taskService;
    private final UserExtractorService userExtractorService;

    @Autowired
    public TaskController(TaskService taskService, UserExtractorService userExtractorService) {
        this.taskService = taskService;
        this.userExtractorService = userExtractorService;
    }

    /**
     * Get a list of tasks associate with specific project with specific progress
     * that aren't passed the due date yet.
     * @param projectId: the id of the projects which we try to find associate tasks
     * @param taskProgress: the progress of the active project tasks we want
     * @return ResponseEntity containing:
     *                      - 200 OK with a list of taskDTO if found or empty list if no task fits requirement
     *                      //toDo:add exception handling
     */

    @GetMapping("/active-project-tasks")
    public ResponseEntity<List<TaskDTO>> getActiveProjectTasksByProgress(@RequestParam long projectId,
                                                             @RequestParam TaskProgress taskProgress) {
        List<TaskDTO> activeProjectTasksByProgress = taskService.getActiveProjectTasksByProgress(projectId, taskProgress);
        return new ResponseEntity<>(activeProjectTasksByProgress, HttpStatus.OK);
    }

    /**
     * Get a list of tasks of the user with specific progress that aren't expired yet
     * @param taskProgress: the progress of the active user tasks we want
     * @param authentication: the authentication token of user who currently login
     * return ResponseEntity contains:
     *                      - 200 OK with a list of taskDTO of user active tasks if found or empty list if no tasks fit
     *                      //toDo: add exception handling
     */
    @GetMapping("/user-active-tasks")
    public ResponseEntity<List<TaskDTO>> getUserActiveTasksByProgress(@RequestParam TaskProgress taskProgress,
                                                                      Authentication authentication) {
        UserDTO user = userExtractorService.extractUser(authentication);
        List<TaskDTO> userActiveTasksWithProgress = taskService.getUserActiveTasksByProgress(user, taskProgress);
        return new ResponseEntity<>(userActiveTasksWithProgress, HttpStatus.OK);
    }

    /**
     * Create the task
     * @param taskCreateDTO: data transfer object containing field needed to create the task object
     * @param authentication: the authentication token of user who currently login
     * @return ResponseEntity contains:
     *                      - 201 Created with taskDTO of new task return if created successfully
     *                      - 400 badRequest if error happen  //toDo:update the exception handling
     */
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

    /**
     * Update the task object with specific id
     * @param taskUpdateRequest: the request contains possible fields for task object
     *                         that are allowed to update
     * @param taskId: the id of the task that user want to update
     * @return ResponseEntity contains:
     *                      - 200OK with TaskDTO of updated task
     *                      - 400 badRequest //toDo: handle the error
     */

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

    /**
     * delete the task with specific id
     * @param taskId: the id of the task
     * @return ResponseEntity contains:
     *                      - 200 OK with message that task is deleted successfully
     *                      - 404 NOT FOUND if the taskId is not found
     */
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
