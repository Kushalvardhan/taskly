package com.taskly.backend.task.dto;

import com.taskly.backend.project.ProjectRole;

public record TaskAssignmentStatusDto(
        Long userId,
        String name,
        String email,
        ProjectRole projectRole,
        boolean assigned
) {}
