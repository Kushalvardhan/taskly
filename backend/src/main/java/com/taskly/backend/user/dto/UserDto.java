package com.taskly.backend.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class UserDto {

    private Long id;

    private String name;

    private String email;

    private Long organisationId;

    private String organisationRole;

    private Instant createdAt;
}
