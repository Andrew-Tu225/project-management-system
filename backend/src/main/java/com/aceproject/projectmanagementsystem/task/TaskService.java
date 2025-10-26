package com.aceproject.projectmanagementsystem.task;

import com.aceproject.projectmanagementsystem.exception.AuthorizationErrorException;
import com.aceproject.projectmanagementsystem.exception.BusinessValidationException;
import com.aceproject.projectmanagementsystem.exception.ResourceNotFoundException;
import com.aceproject.projectmanagementsystem.project.Project;
import com.aceproject.projectmanagementsystem.project.ProjectRepository;
import com.aceproject.projectmanagementsystem.user.User;
import com.aceproject.projectmanagementsystem.user.UserRepository;
import com.aceproject.projectmanagementsystem.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository,  UserRepository userRepository,  ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    public List<TaskDTO> getActiveProjectTasksByProgress(long projectId, TaskProgress taskProgress) {
        if(projectRepository.existsById(projectId)) {
            List<Task> activeProjectTasks = taskRepository.findByProjectBeforeDueDate(projectId);
            List<TaskDTO> activeProjectTaskProgressDTOs = new ArrayList<>();
            for (Task activeProjectTask : activeProjectTasks) {
                if(activeProjectTask.getProgress() == taskProgress) {
                    activeProjectTaskProgressDTOs.add(convertToDTO(activeProjectTask));
                }
            }
            return activeProjectTaskProgressDTOs;
        }
        else{
            throw new ResourceNotFoundException("project with "+ projectId + " not found");
        }
    }

    public List<TaskDTO> getUserActiveTasksByProgress(UserDTO userDTO, TaskProgress taskProgress) {
        List<Task> activeUserTasks = taskRepository.findTasksByPersonBeforeDueDate(userDTO.getEmail());
        List<TaskDTO> activeUserTasksProgressDTOs = new ArrayList<>();
        for (Task activeUserTask : activeUserTasks) {
            if(activeUserTask.getProgress() == taskProgress) {
                activeUserTasksProgressDTOs.add(convertToDTO(activeUserTask));
            }
        }
        return activeUserTasksProgressDTOs;
    }

    public TaskDTO createTask(TaskCreateDTO taskCreateDTO, UserDTO creatorDTO){
        Task task = new Task();
        task.setTitle(taskCreateDTO.getTitle());
        task.setDescription(taskCreateDTO.getDescription());
        task.setImportance(taskCreateDTO.getImportance());
        task.setProgress(taskCreateDTO.getProgress());
        task.setDueDate(taskCreateDTO.getDueDate());
        task.setStartDate(taskCreateDTO.getStartDate());

        //find project through projectId for task if exists else throw NOT FOUND exception
        Optional<Project> project = projectRepository.findById(taskCreateDTO.getProjectId());
        if(project.isPresent()){
            task.setProject(project.get());
        }
        else{
            throw new ResourceNotFoundException("project id "+ taskCreateDTO.getProjectId() + " not found");
        }

        //add task user from taskCreateDTO to task.
        // if taskCreateDTO is empty, add the creator of the task, else add all people in the
        //people field of taskCreateDTO
        if(taskCreateDTO.getPeople().isEmpty()){
            User creator = userRepository.findByEmail(creatorDTO.getEmail()).orElse(null);
            task.getPeople().add(creator);
        }
        else{
            for(UserDTO userDTO: taskCreateDTO.getPeople()){
                User creator = userRepository.findByEmail(userDTO.getEmail()).orElse(null);
                task.getPeople().add(creator);
            }
        }
        Task newTask = taskRepository.save(task);
        return convertToDTO(newTask);
    }

    private TaskDTO convertToDTO(Task newTask) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(newTask.getId());
        taskDTO.setTitle(newTask.getTitle());
        taskDTO.setDescription(newTask.getDescription());
        taskDTO.setImportance(newTask.getImportance());
        taskDTO.setProgress(newTask.getProgress());
        taskDTO.setDueDate(newTask.getDueDate());
        taskDTO.setStartDate(newTask.getStartDate());

        for(User user:newTask.getPeople()){
            UserDTO userDTO = UserDTO.builder()
                    .name(user.getName())
                    .email(user.getEmail())
                    .avatarUrl(user.getAvatarUrl())
                    .build();
            taskDTO.getPeople().add(userDTO);
        }

        TaskProjectDTO taskProjectDTO = TaskProjectDTO.builder()
                .id(newTask.getProject().getId())
                .name(newTask.getProject().getName())
                .build();
        taskDTO.setProject(taskProjectDTO);

        return taskDTO;
    }

    //toDo: add authentication to only allow people in the project which the task belongs to update the task
    public TaskDTO updateTask(TaskUpdateRequest taskUpdateRequest, long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new ResourceNotFoundException("task not found")
        );
        task.setTitle(taskUpdateRequest.getTitle());
        task.setDescription(taskUpdateRequest.getDescription());
        task.setImportance(taskUpdateRequest.getImportance());
        task.setProgress(taskUpdateRequest.getProgress());
        task.setDueDate(taskUpdateRequest.getDueDate());
        task.setStartDate(taskUpdateRequest.getStartDate());

        updatePeopleInTask(taskUpdateRequest.getPeople(), task);

        //if progress is DONE, change field completed to True
        if (task.getProgress() == TaskProgress.DONE) {
            task.setCompleted(true);
        }
        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    /**
     * update the people for this task
     *              - add new person/people that are not in original people list
     *              -remove person/people not in the new people list
     * Constraint
     *  -newTaskPeopleList cannot be empty list or null
     *
     * @param newTaskPeopleList: the new list of user(UserDTO) belongs to this task
     * @param updatedTask: the task that we try to update the field of people
     */
    public void updatePeopleInTask(List<UserDTO> newTaskPeopleList, Task updatedTask){
        //validate TaskUpdateRequest people field contains at least one person
        if(newTaskPeopleList.isEmpty()){
            throw new  BusinessValidationException("task must have at least one person");
        }
        // add user to the task if not already in the original task
        for(UserDTO updatedTaskUserDTO: newTaskPeopleList){
            boolean exist = updatedTask.getPeople()
                    .stream().anyMatch(p -> p.getEmail().equals(updatedTaskUserDTO.getEmail()));
            if(!exist){
                User newUser = userRepository.findByEmail(updatedTaskUserDTO.getEmail()).orElse(null);
                updatedTask.getPeople().add(newUser);
            }
        }
        //remove user from the task if taskUpdateRequest doesn't contain that user
        updatedTask.getPeople().removeIf(u -> newTaskPeopleList.
                stream().noneMatch(p -> p.getEmail().equals(u.getEmail())));
    }

    public void deleteTask(long taskId, UserDTO currentUser) {
        if(taskRepository.existsById(taskId)) {
            Task task = taskRepository.findById(taskId).orElse(null);
            if(task.getPeople().stream().noneMatch(p -> p.getEmail().equals(currentUser.getEmail()))) {
                throw new AuthorizationErrorException("user is not authorized");
            }
            taskRepository.deleteById(taskId);
        }
        else {
            throw new ResourceNotFoundException("task not found");
        }
    }
}
