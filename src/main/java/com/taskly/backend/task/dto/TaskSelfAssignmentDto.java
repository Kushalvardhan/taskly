package com.taskly.backend.task.dto;

import com.taskly.backend.task.TaskAssignee;

public record TaskSelfAssignmentDto(
        String message,
        TaskAssignee assignee
){
}
