package com.taskly.backend.task.dto;

import com.taskly.backend.task.enums.TaskPriority;
import com.taskly.backend.task.enums.TaskType;

import java.time.Instant;

public record UpdateTaskResponseDto(
        Long id,
        String title,
        String description,
        TaskType type,
        TaskPriority priority,
        Instant startDate,
        Instant deadline
) {
}
