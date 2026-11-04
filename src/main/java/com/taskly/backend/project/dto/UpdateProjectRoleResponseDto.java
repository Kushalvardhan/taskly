package com.taskly.backend.project.dto;

import com.taskly.backend.project.ProjectRole;

public record UpdateProjectRoleResponseDto(
      String message,
      Long projectId,
      Long userId,
      ProjectRole role
){}
