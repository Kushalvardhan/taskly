package com.taskly.backend.task.dto.projection;

import com.taskly.backend.task.enums.TaskPriority;
import com.taskly.backend.task.enums.TaskStatus;
import com.taskly.backend.task.enums.TaskType;

import java.time.Instant;

public interface TaskListItemProjection {
    Long getTaskId();

    String getTitle();

    String getDescription();

    TaskType getType();

    TaskPriority getPriority();

    TaskStatus getOverallStatus();

    Instant getDeadline();

    Long getCreatorId();

    String getCreatorName();
}
