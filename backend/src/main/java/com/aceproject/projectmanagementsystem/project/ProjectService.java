package com.aceproject.projectmanagementsystem.project;

import com.aceproject.projectmanagementsystem.dto.CreateProjectRequest;
import com.aceproject.projectmanagementsystem.dto.ProjectDTO;
import com.aceproject.projectmanagementsystem.dto.ProjectUpdateRequest;
import com.aceproject.projectmanagementsystem.dto.UserDTO;
import com.aceproject.projectmanagementsystem.exception.AuthorizationErrorException;
import com.aceproject.projectmanagementsystem.exception.ResourceNotFoundException;
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

    public ProjectDTO createProject(CreateProjectRequest createProjectRequest, UserDTO userDTO) {
        Project project = new Project();
        Optional<User> user = userRepo.findByEmail(userDTO.getEmail());
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User is not found");
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

    /**
     * helper function to get list of emails from User list
     * @param collaboratorsList: list of user from project we try to get email from
     * @return list of emails of collaborators
     */
    private List<String> getEmailFromCollaborators(List<User> collaboratorsList){
        List<String> collaboratorsEmails = new ArrayList<>();
        for (User collaborator:collaboratorsList){
            collaboratorsEmails.add(collaborator.getEmail());
        }
        return collaboratorsEmails;
    }

    /**
     * helper function to check whether user is one of collaborators in this project
     * @param project: the project that we want to check
     * @param userDTO: user currently login
     * @return -true if user is one of collaborators of project
     *         -false otherwise
     */
    private boolean isUserAuthorize(Project project, UserDTO userDTO) {
        List<String> collaboratorEmailList = getEmailFromCollaborators(project.getCollaborators());
        return collaboratorEmailList.contains(userDTO.getEmail());
    }

    public ProjectDTO updateProject(long projectId, ProjectUpdateRequest updateRequest, UserDTO userDTO) {
        Project project = projectRepo.findById(projectId).orElse(null);

        if (project != null) {

            if (isUserAuthorize(project, userDTO)) {
                if (updateRequest.getName() != null) {
                    project.setName(updateRequest.getName());
                }
                if (updateRequest.getDescription() != null) {
                    project.setDescription(updateRequest.getDescription());
                }
                if (updateRequest.getLinks() != null) {
                    project.setLinks(updateRequest.getLinks());
                }
                if(updateRequest.getExpectedFinishDate() != null) {
                    project.setExpectedFinishDate(updateRequest.getExpectedFinishDate());
                }
                projectRepo.save(project);
            }
            else{
                throw new AuthorizationErrorException("user not authorized for this request");
            }
        }
        else{
            throw new ResourceNotFoundException("Project is not found");
        }
        return convertToDTO(project);
    }

    public void deleteProject(long projectId, UserDTO userDTO) {
        Project project = projectRepo.findById(projectId).orElse(null);

        if(project == null){
            throw new ResourceNotFoundException("Project is not found");
        }
        else if(!isUserAuthorize(project, userDTO)){
            throw new AuthorizationErrorException("user not authorized for this request");
        }
        else{
            projectRepo.delete(project);
        }
    }
}
