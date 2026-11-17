package com.taskly.backend.task.dto.projection;

public interface AssigneePreviewProjection {
    Long getTaskId();

    Long getUserId();

    String getName();
}
