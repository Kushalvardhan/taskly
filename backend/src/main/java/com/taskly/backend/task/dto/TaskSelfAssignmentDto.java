package com.taskly.backend.task.dto;

import com.taskly.backend.task.enums.TaskStatus;

public record TaskSelfAssignmentDto(
        Long assignmentId,
        Long taskId,
        TaskStatus status
){
}
