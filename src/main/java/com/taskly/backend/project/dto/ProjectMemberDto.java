package com.taskly.backend.project.dto;

import com.taskly.backend.project.ProjectRole;

import java.time.Instant;

public record ProjectMemberDto(
        Long id,
        Long projectId,
        Long userId,
        ProjectRole role,
        Instant joinedAt
) {}
