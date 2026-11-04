package com.taskly.backend.project.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private Long organisationId;
    private String createdBy;
    private Instant createdAt;
};
