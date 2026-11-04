package com.taskly.backend.project.dto;

import java.util.List;

public record ProjectMemberListResponseDto(
        String message,
        Integer membersCount,
        List<ProjectMemberListDto> members
){}
