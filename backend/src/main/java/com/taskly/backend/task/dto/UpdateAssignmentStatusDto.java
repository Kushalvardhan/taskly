package com.taskly.backend.task.dto;

import com.taskly.backend.task.enums.TaskStatus;

import java.time.Instant;

public record UpdateAssignmentStatusDto(
        Long taskId,
        Long userId,
        TaskStatus status,
        Instant updatedAt
) {
}
