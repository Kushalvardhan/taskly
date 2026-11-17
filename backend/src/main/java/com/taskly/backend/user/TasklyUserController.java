package com.taskly.backend.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class TasklyUserController {
    private final TasklyUserService userService;

    public TasklyUserController(TasklyUserService userService){
        this.userService = userService;
    }

}
