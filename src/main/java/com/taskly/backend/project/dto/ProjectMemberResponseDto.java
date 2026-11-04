package com.taskly.backend.project.dto;

public record ProjectMemberResponseDto(
        String message,
        ProjectMemberDto projectMember
) {}
