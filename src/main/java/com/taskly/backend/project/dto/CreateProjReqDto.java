package com.taskly.backend.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjReqDto(

        @NotBlank(message = "Project name is required")
        @Size(max = 100, message = "Project name cannot exceed 100 characters")
        String name,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description
) {}
