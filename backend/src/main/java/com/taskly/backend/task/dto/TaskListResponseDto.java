package com.taskly.backend.task.dto;

import java.util.List;

public record TaskListResponseDto(
        List<TaskListItemResponseDto> content,

        int page,

        int size,

        long totalElements,

        int totalPages,

        boolean last
) {}
