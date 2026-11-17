package com.taskly.backend.task.dto;

import com.taskly.backend.task.enums.TaskPriority;
import com.taskly.backend.task.enums.TaskType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record UpdateTaskRequestDto(
        @Size(max = 150, message = "Task title must not exceed 150 characters")
        String title,

        @Size(max = 2000, message = "Task description must not exceed 2000 characters")
        String description,

        @FutureOrPresent(message = "Start date must be in the present or future")
        Instant startDate,

        @Future(message = "Deadline must be in the future")
        Instant deadline,

        TaskType type,

        TaskPriority priority
){}
