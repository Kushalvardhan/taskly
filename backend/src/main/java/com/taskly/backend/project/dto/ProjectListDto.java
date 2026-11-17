package com.taskly.backend.project.dto;

import com.taskly.backend.project.ProjectRole;

public record ProjectListDto(
        Long id,
        String name,
        Long memberCount,
        boolean joined,
        ProjectRole myRole
) {}