package com.taskly.backend.user.dto;

public record LoginResponseDto(UserDto user, String jwtToken) {
}
