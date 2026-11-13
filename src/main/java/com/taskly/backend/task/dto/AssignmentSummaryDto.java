package com.taskly.backend.task.dto;

public record AssignmentSummaryDto(
        Long totalAssignees,

        Long completed,

        Long inProgress,

        Long todo,

        Long cancelled
) {}
