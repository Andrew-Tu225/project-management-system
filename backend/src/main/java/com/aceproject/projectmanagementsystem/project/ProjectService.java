package com.aceproject.projectmanagementsystem.project;

import com.aceproject.projectmanagementsystem.dto.CreateProjectRequest;
import com.aceproject.projectmanagementsystem.dto.ProjectDTO;
import com.aceproject.projectmanagementsystem.dto.ProjectUpdateRequest;
import com.aceproject.projectmanagementsystem.dto.UserDTO;
import com.aceproject.projectmanagementsystem.user.User;
import com.aceproject.projectmanagementsystem.user.UserRepository;
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
        projectDTO.setId(project.getId());
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

    public ProjectDTO updateProject(long projectId, ProjectUpdateRequest updateRequest, UserDTO userDTO) throws Exception {
        Project project = projectRepo.findById(projectId).get();

        List<String> collaboratorsEmail = new ArrayList<>();
        for (User collaborator:project.getCollaborators()){
            collaboratorsEmail.add(collaborator.getEmail());
        }

        if (collaboratorsEmail.contains(userDTO.getEmail())) {
            if (updateRequest.getName() != null) {
                project.setName(updateRequest.getName());
            }
            if (updateRequest.getDescription() != null) {
                project.setDescription(updateRequest.getDescription());
            }
            if (updateRequest.getLinks() != null) {
                project.setLinks(updateRequest.getLinks());
            }
            projectRepo.save(project);
            return convertToDTO(project);
        }
        else{
            throw new Exception("user not authorized");
        }
    }

    public void deleteProject(long projectId, UserDTO userDTO) throws Exception {
        Project project = projectRepo.findById(projectId).get();

        List<String> collaboratorsEmail = new ArrayList<>();
        for (User collaborator:project.getCollaborators()){
            collaboratorsEmail.add(collaborator.getEmail());
        }

        if(collaboratorsEmail.contains(userDTO.getEmail())){
            projectRepo.deleteById(projectId);
        }
        else{
            throw new Exception("user not authorized");
        }
    }
}
