package com.taskly.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TasklyController {

    @GetMapping("/home")
    public String sayHello(){
        return "Hello There!";
    }
}
