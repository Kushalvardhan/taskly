package com.taskly.backend.task.dto;

import com.taskly.backend.task.enums.TaskStatus;

public record MyAssignmentDto(
        boolean assigned,

        TaskStatus status
) {}
