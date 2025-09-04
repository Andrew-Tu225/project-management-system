package com.aceproject.projectmanagementsystem.backend.user;

import com.aceproject.projectmanagementsystem.dto.UserDTO;
import com.aceproject.projectmanagementsystem.dto.UserRegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<UserDTO> addUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        try{
            UserDTO userdTO = userService.addUser(userRegisterDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(userdTO);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable long id) {
        UserDTO userDTO = userService.getUserByID(id);
        if (userDTO == null){
            return ResponseEntity.notFound().build();
        }
        else{
            return ResponseEntity.ok().body(userDTO);
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable long id, @RequestBody UserRegisterDTO userRegisterDTO) {
        if(userService.getUserByID(id) == null){
            return ResponseEntity.notFound().build();
        }
        else{
            UserDTO userDTO = userService.updateUser(id, userRegisterDTO);
            return ResponseEntity.ok().body(userDTO);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        if(userService.getUserByID(id) == null){
            return ResponseEntity.notFound().build();
        }
        else{
            userService.deleteUser(id);
            return ResponseEntity.ok().body("User has been deleted successfully");
        }
    }
}
