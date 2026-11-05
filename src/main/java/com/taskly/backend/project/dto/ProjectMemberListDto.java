package com.taskly.backend.project.dto;

import com.taskly.backend.project.ProjectRole;

public record ProjectMemberListDto(
        Long userId,
        String memberName,
        String memberEmail,
        ProjectRole role
){}
