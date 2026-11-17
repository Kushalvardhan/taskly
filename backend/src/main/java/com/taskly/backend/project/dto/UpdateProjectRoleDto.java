package com.taskly.backend.project.dto;

import com.taskly.backend.project.ProjectRole;

public record UpdateProjectRoleDto(
      Long projectId,
      Long userId,
      ProjectRole role
){}
