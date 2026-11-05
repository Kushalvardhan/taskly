package com.taskly.backend.task.dto;

public record CreateTaskResponseDto(
        String message,
        TaskResponseDto task
) {}
