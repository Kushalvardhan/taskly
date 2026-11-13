package com.taskly.backend.task.dto.projection;

public interface AssignmentSummaryProjection {
    Long getTaskId();

    Long getTotalAssignees();

    Long getCompleted();

    Long getInProgress();

    Long getTodo();

    Long getCancelled();
}
