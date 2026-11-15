package com.taskly.backend.task.dto.projection;

import com.taskly.backend.project.ProjectRole;
import com.taskly.backend.task.enums.TaskStatus;

public interface TaskAssigneeDetailsProjection {

    Long getUserId();

    String getName();

    ProjectRole getProjectRole();

    TaskStatus getStatus();
}
