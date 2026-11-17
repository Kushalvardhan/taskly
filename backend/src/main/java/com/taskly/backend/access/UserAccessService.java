package com.taskly.backend.access;

import com.taskly.backend.exception.ResourceNotFoundException;
import com.taskly.backend.user.TasklyUser;
import com.taskly.backend.user.TasklyUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAccessService {

    private final TasklyUserRepository tasklyUserRepository;

    public TasklyUser getUserByEmail(String email) {

        return tasklyUserRepository.findUserByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );
    }
}