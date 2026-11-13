package com.taskly.backend.task.dto;

import com.taskly.backend.task.enums.TaskPriority;
import com.taskly.backend.task.enums.TaskStatus;
import com.taskly.backend.task.enums.TaskType;

import java.time.Instant;
import java.util.List;

public record TaskListItemResponseDto(
        Long taskId,

        String title,

        String description,

        TaskType type,

        TaskPriority priority,

        TaskStatus overallStatus,

        Instant deadline,

        CreatedByDto createdBy,

        AssignmentSummaryDto assignmentSummary,

        MyAssignmentDto myAssignment,

        List<AssigneePreviewDto> assigneePreview
) {
}
