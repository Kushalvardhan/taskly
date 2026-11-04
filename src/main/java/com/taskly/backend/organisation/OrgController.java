package com.taskly.backend.organisation;

import com.taskly.backend.organisation.dto.*;
import com.taskly.backend.security.util.JwtUtil;
import com.taskly.backend.user.TasklyUser;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organisation")
@RequiredArgsConstructor
public class OrgController {

    private final OrgService orgService;
    private final JwtUtil jwtUtil;

    // CREATE ORGANISATION
    @PostMapping("/register")
    public ResponseEntity<OrgAuthResponseDto> registerOrg(
            @RequestBody CreateOrgRequestDto request,
            Authentication authentication
    ) {

        OrgOperationResult result = orgService.registerOrg(
                request,
                authentication.getName()
        );

        return buildResponse("Organisation created successfully", result);
    }

    // JOIN ORGANISATION
    @PostMapping("/join")
    public ResponseEntity<OrgAuthResponseDto> joinOrg(
            @RequestBody JoinOrgRequestDto request,
            Authentication authentication
    ) {

        OrgOperationResult result = orgService.joinOrg(
                request,
                authentication.getName()
        );

        return buildResponse("Organisation joined successfully", result);
    }

    // COMMON RESPONSE BUILDER
    private ResponseEntity<OrgAuthResponseDto> buildResponse(String message, OrgOperationResult result) {

        Organisation org = result.organisation();
        TasklyUser user = result.user();

        // Organisation DTO
        OrganisationResponseDto orgDto =
                new OrganisationResponseDto(
                        org.getId(),
                        org.getName(),
                        org.getCode(),
                        org.getCreatedBy().getId(),
                        org.getCreatedAt(),
                        org.getUpdatedAt()
                );

        // Updated authenticated user DTO
        OrganisationUserDto userDto =
                new OrganisationUserDto(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getOrganisationRole().name()
                );

        // Generate new JWT with updated role
        String newJwtToken = jwtUtil.generateJwtToken(user);

        // Final response
        OrgAuthResponseDto response =
                new OrgAuthResponseDto(
                        message,
                        orgDto,
                        userDto,
                        newJwtToken
                );

        return ResponseEntity.ok(response);
    }
}