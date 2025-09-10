package com.aceproject.projectmanagementsystem.backend.project;

import com.aceproject.projectmanagementsystem.backend.dto.CreateProjectRequest;
import com.aceproject.projectmanagementsystem.backend.dto.ProjectDTO;
import com.aceproject.projectmanagementsystem.backend.dto.UserDTO;
import com.aceproject.projectmanagementsystem.backend.user.User;
import com.aceproject.projectmanagementsystem.backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepo;
    private final UserRepository userRepo;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepo = projectRepository;
        this.userRepo = userRepository;
    }

    public ProjectDTO convertToDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        List<UserDTO> collaboratorsDTOs = new ArrayList<>();
        projectDTO.setName(project.getName());
        projectDTO.setDescription(project.getDescription());

        for(User collaborator:project.getCollaborators()){
            UserDTO userDTO = UserDTO.builder()
                    .name(collaborator.getName())
                    .email(collaborator.getEmail())
                    .avatarUrl(collaborator.getAvatarUrl())
                    .build();
            collaboratorsDTOs.add(userDTO);
        }

        projectDTO.setCollaborators(collaboratorsDTOs);
        projectDTO.setLinks(project.getLinks());
        projectDTO.setCreationDate(project.getCreationDate());
        projectDTO.setExpectedFinishDate(project.getExpectedFinishDate());
        return projectDTO;
    }

    public ProjectDTO createProject(CreateProjectRequest createProjectRequest, UserDTO userDTO) throws Exception {
        Project project = new Project();
        Optional<User> user = userRepo.findByEmail(userDTO.getEmail());
        if (user.isEmpty()) {
            throw new Exception("user not found");
        }
        else{
            project.setName(createProjectRequest.getProjectName());
            project.setDescription(createProjectRequest.getProjectDescription());
            project.setCollaborators(List.of(user.get()));
            project.setCreationDate(createProjectRequest.getCreationDate());
            project.setExpectedFinishDate(createProjectRequest.getExpectedFinishDate());
            projectRepo.save(project);
            return convertToDTO(project);
        }
    }

}
