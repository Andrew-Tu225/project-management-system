package com.aceproject.projectmanagementsystem.backend.user;

import com.aceproject.projectmanagementsystem.backend.dto.ProjectDTO;
import com.aceproject.projectmanagementsystem.backend.dto.UserDTO;
import com.aceproject.projectmanagementsystem.backend.project.Project;
import com.aceproject.projectmanagementsystem.backend.project.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final ProjectService projectService;

    @Autowired
    public UserService(UserRepository userRepo, ProjectService projectService) {
        this.userRepo = userRepo;
        this.projectService = projectService;
    }

    public UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    public UserDTO getUserByEmail(String email) {
        Optional<User> user = userRepo.findByEmail(email);
        if(user.isPresent()) {
            return convertToDTO(user.get());
        }
        else{
            return null;
        }
    }

    public List<ProjectDTO> getProjects(UserDTO userDTO) throws Exception {
        Optional<User> user = userRepo.findByEmail(userDTO.getEmail());
        List<ProjectDTO> projectDTOList = new ArrayList<>();

        if(user.isPresent()) {
            List<Project> projects = user.get().getProjects();
            if(projects.isEmpty()){
                return new ArrayList<>();
            }
            for(Project project : projects) {
                projectDTOList.add(projectService.convertToDTO(project));
            }
            return projectDTOList;
        }
        else{
            throw new Exception("user not found");
        }
    }
}
