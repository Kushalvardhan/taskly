package com.taskly.backend.user;

import com.taskly.backend.user.dto.CreateUserRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody CreateUserRequestDto userDto){
        log.info("user request body : {}", userDto);
        User registeredUser = userService.registerUser(userDto);
        return ResponseEntity.ok(registeredUser);
    }

}
