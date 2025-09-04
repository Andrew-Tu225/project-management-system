package com.aceproject.projectmanagementsystem.backend.user;

import com.aceproject.projectmanagementsystem.dto.UserDTO;
import com.aceproject.projectmanagementsystem.dto.UserRegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepo;

    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

    public UserDTO getUserByID(long id) {
        User user = userRepo.findById(id).get();
        return convertToDTO(user);
    }

    public UserDTO addUser(UserRegisterDTO userRegisterDTO) {
        User user = new User();
        user.setName(userRegisterDTO.getName());
        user.setEmail(userRegisterDTO.getEmail());
        user.setPassword(userRegisterDTO.getPassword());

        return convertToDTO(userRepo.save(user));
    }

    public UserDTO updateUser(long id, UserRegisterDTO userRegisterDTO) {
        User user = userRepo.findById(id).get();
        user.setName(userRegisterDTO.getName());
        user.setEmail(userRegisterDTO.getEmail());
        user.setPassword(userRegisterDTO.getPassword());
        return convertToDTO(userRepo.save(user));
    }

    public void deleteUser(long userId) {
        userRepo.deleteById(userId);
    }
}
