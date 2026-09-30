package com.taskly.backend.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateUserRequestDto {

    private String name;

    private String email;

    private String passwordHash;
}
