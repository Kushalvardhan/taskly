package com.taskly.backend.task.dto;

import com.taskly.backend.task.enums.TaskPriority;
import com.taskly.backend.task.enums.TaskStatus;
import com.taskly.backend.task.enums.TaskType;

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
