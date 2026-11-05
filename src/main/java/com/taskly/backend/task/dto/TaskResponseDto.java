package com.taskly.backend.task.dto;

import com.taskly.backend.task.TaskPriority;
import com.taskly.backend.task.TaskStatus;
import com.taskly.backend.task.TaskType;

import java.time.Instant;

public record TaskResponseDto(
        Long id,
        Long projectId,
        Long creatorId,
        String title,
        String description,
        Long assigneeId,
        Instant startDate,
        Instant deadline,
        TaskType type,
        TaskPriority priority,
        TaskStatus status,
        Instant createdAt
) {}
