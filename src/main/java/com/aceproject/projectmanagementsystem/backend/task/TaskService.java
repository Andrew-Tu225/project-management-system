package com.aceproject.projectmanagementsystem.backend.task;

import com.aceproject.projectmanagementsystem.backend.dto.*;
import com.aceproject.projectmanagementsystem.backend.project.Project;
import com.aceproject.projectmanagementsystem.backend.project.ProjectRepository;
import com.aceproject.projectmanagementsystem.backend.user.User;
import com.aceproject.projectmanagementsystem.backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        List<Task> activeProjectTasks = taskRepository.findByProjectBeforeDueDate(projectId);
        List<TaskDTO> activeProjectTaskProgressDTOs = new ArrayList<>();
        for (Task activeProjectTask : activeProjectTasks) {
            System.out.println(activeProjectTask.getTitle());
            if(activeProjectTask.getProgress() == taskProgress) {
                activeProjectTaskProgressDTOs.add(convertToDTO(activeProjectTask));
            }
        }
        return activeProjectTaskProgressDTOs;
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

        Project project = projectRepository.findById(taskCreateDTO.getProjectId()).get();
        task.setProject(project);

        if(taskCreateDTO.getPeople().isEmpty()){
            User creator = userRepository.findByEmail(creatorDTO.getEmail()).get();
            task.getPeople().add(creator);
        }
        else{
            for(UserDTO userDTO: taskCreateDTO.getPeople()){
                User creator = userRepository.findByEmail(userDTO.getEmail()).get();
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

    public TaskDTO updateTask(TaskUpdateRequest taskUpdateRequest, long taskId) {
        Task task = taskRepository.findById(taskId).get();
        task.setTitle(taskUpdateRequest.getTitle());
        task.setDescription(taskUpdateRequest.getDescription());
        task.setImportance(taskUpdateRequest.getImportance());
        task.setProgress(taskUpdateRequest.getProgress());
        task.setDueDate(taskUpdateRequest.getDueDate());
        task.setStartDate(taskUpdateRequest.getStartDate());

        //add updated user list to task
        for(UserDTO userDTO: taskUpdateRequest.getPeople()){
            User newUser = userRepository.findByEmail(userDTO.getEmail()).orElse(null);
            task.getPeople().add(newUser);
        }

        //if progress is DONE, change field completed to True
        if (task.getProgress() == TaskProgress.DONE) {
            task.setCompleted(true);
        }
        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    public void deleteTask(long taskId) {
        taskRepository.deleteById(taskId);
    }
}
