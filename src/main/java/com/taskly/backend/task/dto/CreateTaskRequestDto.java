package com.taskly.backend.task.dto;

import com.taskly.backend.task.TaskPriority;
import com.taskly.backend.task.TaskType;
import jakarta.validation.constraints.*;

import java.time.Instant;

public record CreateTaskRequestDto(
        @NotBlank(message = "Task title is required")
        @Size(max = 150, message = "Task title must not exceed 150 characters")
        String title,

        @Size(max = 2000, message = "Task description must not exceed 2000 characters")
        String description,

        @FutureOrPresent(message = "Start date must be in the present or future")
        Instant startDate,

        @Future(message = "Deadline must be in the future")
        Instant deadline,

        @NotNull(message = "Task type is required")
        TaskType type,

        @NotNull(message = "Task priority is required")
        TaskPriority priority
) {}
