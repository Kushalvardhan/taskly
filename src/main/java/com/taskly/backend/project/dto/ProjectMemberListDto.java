package com.taskly.backend.project.dto;

import com.taskly.backend.project.ProjectRole;

public record ProjectMemberListDto(
        Long id,
        Long projectId,
        Long userId,
        String memberName,
        String memberEmail,
        ProjectRole role
){}
