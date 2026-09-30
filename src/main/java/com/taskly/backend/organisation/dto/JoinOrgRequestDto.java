package com.taskly.backend.organisation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinOrgRequestDto {

    private Long id;

    private String organisationCode;
}
