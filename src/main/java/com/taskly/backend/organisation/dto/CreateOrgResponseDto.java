package com.taskly.backend.organisation.dto;

import com.taskly.backend.user.dto.CreateOrgResponseUserDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateOrgResponseDto {
    private Long id;

    private String name;

    private String code;

    private CreateOrgResponseUserDto createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
