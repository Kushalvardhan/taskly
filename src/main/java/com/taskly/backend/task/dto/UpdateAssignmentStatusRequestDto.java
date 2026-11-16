package com.taskly.backend.task.dto;

import com.taskly.backend.task.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateAssignmentStatusRequestDto(

        @NotNull(message = "Status is required")
        TaskStatus status
) {}
