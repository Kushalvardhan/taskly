package com.taskly.backend.user;

import org.springframework.stereotype.Service;

@Service
public class TasklyUserService {

    private final TasklyUserRepository tasklyUserRepository;

    public TasklyUserService(TasklyUserRepository tasklyUserRepository) {
        this.tasklyUserRepository = tasklyUserRepository;
    }

}
