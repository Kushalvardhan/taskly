package com.taskly.backend.organisation.dto;

public record OrganisationUserDto(
        Long id,
        String name,
        String email,
        String organisationRole
) {}