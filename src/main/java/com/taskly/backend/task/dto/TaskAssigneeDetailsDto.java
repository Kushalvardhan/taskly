package com.taskly.backend.task.dto;

import com.taskly.backend.project.ProjectRole;
import com.taskly.backend.task.enums.TaskStatus;

public record TaskAssigneeDetailsDto(
        Long userId,
        String name,
        ProjectRole projectRole,
        TaskStatus status
) {}
