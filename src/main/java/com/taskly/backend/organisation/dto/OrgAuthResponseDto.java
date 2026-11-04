package com.taskly.backend.organisation.dto;

public record OrgAuthResponseDto(
        String message,
        OrganisationResponseDto organisation,
        OrganisationUserDto user,
        String jwtToken
) {}