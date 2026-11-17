package com.taskly.backend.organisation.dto;

import java.time.Instant;

public record OrganisationResponseDto(
        Long id,
        String name,
        String code,
        Long createdById,
        Instant createdAt,
        Instant updatedAt
) {}