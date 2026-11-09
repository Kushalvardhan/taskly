package com.taskly.backend.task.dto;

import java.util.List;

public record GetTaskAssignmentStatusResponse(
        String message,
        List<TaskAssignmentStatusDto> team
) {
}
