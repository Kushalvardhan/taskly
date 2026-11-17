package com.taskly.backend.organisation;

import com.taskly.backend.common.ApiResponse;
import com.taskly.backend.organisation.dto.*;
import com.taskly.backend.security.util.JwtUtil;
import com.taskly.backend.user.TasklyUser;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/organisation")
@RequiredArgsConstructor
public class OrgController {

    private final OrgService orgService;
    private final JwtUtil jwtUtil;

    // CREATE ORGANISATION
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<OrgAuthDataDto>> registerOrg(
            @RequestBody CreateOrgRequestDto request,
            Authentication authentication
    ) {
        OrgAuthDataDto data = orgService.registerOrg(
                request,
                authentication.getName()
        );

        ApiResponse<OrgAuthDataDto> response =
                new ApiResponse<>(
                        "Organisation created successfully",
                        HttpStatus.CREATED.value(),
                        Instant.now(),
                        data
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<OrgAuthDataDto>> joinOrg(
            @RequestBody JoinOrgRequestDto request,
            Authentication authentication
    ) {

        OrgAuthDataDto data = orgService.joinOrg(
                request,
                authentication.getName()
        );

        ApiResponse<OrgAuthDataDto> response =
                new ApiResponse<>(
                        "Organisation joined successfully",
                        HttpStatus.OK.value(),
                        Instant.now(),
                        data
                );

        return ResponseEntity.ok(response);
    }
}