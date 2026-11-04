package com.taskly.backend.organisation.dto;

import com.taskly.backend.organisation.Organisation;
import com.taskly.backend.user.TasklyUser;

public record OrgOperationResult(
        Organisation organisation,
        TasklyUser user
) {
}