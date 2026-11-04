package com.taskly.backend.project.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectResponseDto {

    private String message;
    private ProjectDto project;
}
