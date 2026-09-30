package com.taskly.backend.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrgResponseUserDto {
    private Long id;

    private String name;

    private String email;

    private String organisationRole;
}
