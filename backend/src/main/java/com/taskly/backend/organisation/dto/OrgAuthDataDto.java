package com.taskly.backend.organisation.dto;

public record OrgAuthDataDto(
        OrganisationResponseDto organisation,
        OrganisationUserDto user,
        String jwtToken
) {}
