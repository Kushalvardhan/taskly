package com.taskly.backend.task.dto.projection;

import com.taskly.backend.task.enums.TaskStatus;

public interface MyAssignmentProjection {
    Long getTaskId();

    TaskStatus getStatus();
}
