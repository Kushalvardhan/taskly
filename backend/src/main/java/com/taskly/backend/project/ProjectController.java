package com.taskly.backend.project;

import com.taskly.backend.common.ApiResponse;
import com.taskly.backend.project.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectListDto>>> getAllProjects(
            Authentication authentication
    ) {

        String email = authentication.getName();

        List<ProjectListDto> data =
                projectService.getAllProjects(email);

        ApiResponse<List<ProjectListDto>> response =
                new ApiResponse<>(
                        "Projects retrieved successfully",
                        HttpStatus.OK.value(),
                        Instant.now(),
                        data
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ViewProjectDto>> getProject(
            Authentication authentication,
            @PathVariable Long projectId
    ) {
        String email = authentication.getName();

        ViewProjectDto data =
                projectService.getProject(email, projectId);

        ApiResponse<ViewProjectDto> response =
                new ApiResponse<>(
                        "Project details retrieved successfully",
                        HttpStatus.OK.value(),
                        Instant.now(),
                        data
                );

        return ResponseEntity.ok(response);
    }

    //Create Project
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ProjectDto>> createProject(
            @Valid @RequestBody CreateProjReqDto request,
            Authentication authentication
    ) {
        OrgProject result = projectService.createProject(
                request,
                authentication.getName()
        );

        return builder("Project Created Successfully!", result);
    }

    //Join Project
    @PostMapping("/{projectId}/join")
    public ResponseEntity<ApiResponse<ProjectMemberDto>> joinProject(
            Authentication authentication,
            @PathVariable Long projectId
    ) {

        String email = authentication.getName();

        ProjectMemberDto data =
                projectService.joinProject(email, projectId);

        ApiResponse<ProjectMemberDto> response =
                new ApiResponse<>(
                        "Successfully Joined the Project",
                        HttpStatus.CREATED.value(),
                        Instant.now(),
                        data
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    public ResponseEntity<ApiResponse<ProjectDto>> builder(
            String message,
            OrgProject project
    ) {
        // Create DTO
        ProjectDto projectDto = new ProjectDto();

        // Copy properties
        BeanUtils.copyProperties(project, projectDto);

        // Manually set ID fields
        projectDto.setOrganisationId(
                project.getOrganisation().getId()
        );

        projectDto.setCreatedBy(
                project.getCreatedBy()
        );

        ApiResponse<ProjectDto> response =
                new ApiResponse<>(
                        message,
                        HttpStatus.CREATED.value(),
                        Instant.now(),
                        projectDto
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<ApiResponse<List<ProjectMemberListDto>>> getAllMembers(
            Authentication authentication,
            @PathVariable Long projectId
    ){
        String email = authentication.getName();

        List<ProjectMemberListDto> data =
                projectService.getMembers(email, projectId);

        ApiResponse<List<ProjectMemberListDto>> response =
                new ApiResponse<>(
                        "Successfully fetched Project members",
                        HttpStatus.OK.value(),
                        Instant.now(),
                        data
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{projectId}/members/{userId}/role")
    public ResponseEntity<ApiResponse<UpdateProjectRoleDto>> projectRoleUpdate(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateProjectRoleRequestDto request
    ){
        UpdateProjectRoleDto data =
                projectService.projectRoleUpdate(
                        authentication.getName(),
                        projectId,
                        userId,
                        request
                );

        ApiResponse<UpdateProjectRoleDto> response =
                new ApiResponse<>(
                        "Project member role updated successfully",
                        HttpStatus.OK.value(),
                        Instant.now(),
                        data
                );

        return ResponseEntity.ok(response);
    }
}
