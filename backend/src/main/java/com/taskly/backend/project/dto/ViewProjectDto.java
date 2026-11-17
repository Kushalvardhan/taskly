package com.taskly.backend.project.dto;

import java.time.Instant;

public record ViewProjectDto(
        Long id,
        String name,
        String description,
        String orgName,
        String orgCreatorName,
        Instant createdAt
) {}
