package com.aceproject.projectmanagementsystem.user;

import com.aceproject.projectmanagementsystem.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserExtractorService userExtractor;

    @Autowired
    public UserController(UserService userService,  UserExtractorService userExtractor) {
        this.userService = userService;
        this.userExtractor = userExtractor;
    }

    @GetMapping("/me")
    public UserDTO getCurrentUserInfo(Authentication authentication) {
        UserDTO userDTO = userExtractor.extractUser(authentication);
        return userService.getUserByEmail(userDTO.getEmail());
    }
}
