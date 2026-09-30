package com.taskly.backend.organisation.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateOrgRequestDto {
    private String name;

    private String code;

    private Long createdByUserId;
}
