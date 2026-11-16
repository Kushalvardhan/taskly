package com.taskly.backend.common;

import java.time.Instant;

public record ApiResponse<T>(
        String message,
        int status,
        Instant timestamp,
        T data
) {}
