package com.taskly.backend.user.dto;

public record LoginResponseDto(String message, UserDto user, String jwtToken) {
}
