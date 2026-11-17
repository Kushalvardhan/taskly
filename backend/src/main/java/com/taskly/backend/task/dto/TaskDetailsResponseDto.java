package com.taskly.backend.task.dto;

import com.taskly.backend.task.enums.TaskPriority;
import com.taskly.backend.task.enums.TaskStatus;
import com.taskly.backend.task.enums.TaskType;

import java.time.Instant;
import java.util.List;

public record TaskDetailsResponseDto(
        Long taskId,
        String title,
        String description,
        TaskType type,
        TaskPriority priority,
        TaskStatus overallStatus,
        Instant startDate,
        Instant deadline,
        CreatedByDto createdBy,
        AssignmentSummaryDto assignmentSummary,
        MyAssignmentDto myAssignment,
        List<TaskAssigneeDetailsDto> assignees
) {}
