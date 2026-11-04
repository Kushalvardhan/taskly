package com.taskly.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.config.Task;

import java.util.Optional;

public interface TasklyUserRepository extends JpaRepository<TasklyUser, Long> {
    Optional<TasklyUser> findUserByEmail(String username);
}
