package com.taskly.backend.task.dto;

import java.util.List;

public record GetTaskAssignmentOptionsResponse(
        String message,
        List<TaskAssignmentOptionDto> team
) {
}
