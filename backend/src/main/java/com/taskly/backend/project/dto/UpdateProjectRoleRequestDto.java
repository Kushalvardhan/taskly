package com.taskly.backend.project.dto;

import com.taskly.backend.project.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateProjectRoleRequestDto(
        @NotNull(message = "Project role is required")
        ProjectRole role
) {}
