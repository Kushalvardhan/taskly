package com.taskly.backend.project;

import com.taskly.backend.project.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectListDto>> getAllProjects(
            Authentication authentication
    ) {

        String email = authentication.getName();

        List<ProjectListDto> projects =
                projectService.getAllProjects(email);

        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ViewProjectDto> getProject(
            Authentication authentication,
            @PathVariable Long projectId) {

        String email = authentication.getName();

        ViewProjectDto project = projectService.getProject(email, projectId);

        return ResponseEntity.ok(project);

    }

    //Create Project
    @PostMapping("/create")
    public ResponseEntity<ProjectResponseDto> createProject(
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
    public ResponseEntity<ProjectMemberResponseDto> joinProject(
            Authentication authentication,
            @PathVariable Long projectId) {

        String email = authentication.getName();
        ProjectMemberDto projectMember = projectService.joinProject(email, projectId);

        ProjectMemberResponseDto response =
                new ProjectMemberResponseDto(
                        "Successfully Joined the Project",
                        projectMember
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<ProjectResponseDto> builder(
            String message,
            OrgProject project
    ) {
        //Create Dto
        ProjectDto projectDto = new ProjectDto();

        //Copy Properties
        BeanUtils.copyProperties(project, projectDto);

        //Manually set Id fields
        projectDto.setOrganisationId(
                project.getOrganisation().getId()
        );

        projectDto.setCreatedBy(
                project.getCreatedBy()
        );

        //Response Creation
        ProjectResponseDto response =
                ProjectResponseDto.builder()
                        .message(message)
                        .project(projectDto)
                        .build();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<ProjectMemberListResponseDto> getAllMembers(
            Authentication authentication,
            @PathVariable Long projectId
    ){
        String email = authentication.getName();

        List<ProjectMemberListDto> members =
                projectService.getMembers(email, projectId);

        ProjectMemberListResponseDto response = new ProjectMemberListResponseDto(
                "Successfully fetched Project members",
                members.size(),
                members
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{projectId}/members/{userId}/role")
    public ResponseEntity<?> projectRoleUpdate(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateProjectRoleRequestDto request
    ){
        UpdateProjectRoleResponseDto response =
                projectService.projectRoleUpdate(
                        authentication.getName(),
                        projectId,
                        userId,
                        request
                );
        return ResponseEntity.ok(response);
    }
}
