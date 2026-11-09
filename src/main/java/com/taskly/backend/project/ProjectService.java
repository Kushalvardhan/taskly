package com.taskly.backend.project;

import com.taskly.backend.access.ProjectAccessService;
import com.taskly.backend.exception.ResourceNotFoundException;
import com.taskly.backend.organisation.Organisation;
import com.taskly.backend.organisation.OrganisationRole;
import com.taskly.backend.project.dto.*;
import com.taskly.backend.user.TasklyUser;
import com.taskly.backend.user.TasklyUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TasklyUserRepository tasklyUserRepository;
    private final ProjectAccessService projectAccessService;

    @Transactional(readOnly = true)
    public List<ProjectListDto> getAllProjects(String email) {
        TasklyUser user = projectAccessService.getUserByEmail(email);

        Organisation organisation =
                projectAccessService.getUserOrganisation(user);

        return projectRepository.findProjectSummaries(
                organisation.getId(),
                user.getId()
        );
    }

    //Create Project (Organisation Manager)
    @Transactional
    public OrgProject createProject(CreateProjReqDto request, String email) {

        TasklyUser user = projectAccessService.getUserByEmail(email);

        projectAccessService.verifyOrganisationManager(user);

        Organisation organisation =
                projectAccessService.getUserOrganisation(user);

        OrgProject project = new OrgProject();

        project.setName(request.name());
        project.setDescription(request.description());
        project.setOrganisation(organisation);

        OrgProject savedProject = projectRepository.save(project);

        // Add creator as project manager
        ProjectMember member = new ProjectMember();

        member.setProject(savedProject);
        member.setUser(user);
        member.setProjectRole(ProjectRole.PROJECT_MANAGER);

        projectMemberRepository.save(member);

        return savedProject;
    }

    //Join Project
    @Transactional
    public ProjectMemberDto joinProject(String email, Long projectId) {
        TasklyUser user = projectAccessService.getUserByEmail(email);

        OrgProject project =
                projectAccessService.getProjectForUser(projectId, user);

        boolean alreadyJoined =
                projectMemberRepository.existsByProjectAndUser(project, user);

        if (alreadyJoined) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You have already joined this project"
            );
        }

        ProjectMember projectMember = new ProjectMember();

        projectMember.setProject(project);
        projectMember.setUser(user);
        projectMember.setProjectRole(ProjectRole.MEMBER);

        ProjectMember savedMember =
                projectMemberRepository.save(projectMember);

        return new ProjectMemberDto(
                savedMember.getId(),
                savedMember.getProject().getId(),
                savedMember.getUser().getId(),
                savedMember.getProjectRole(),
                savedMember.getJoinedAt()
        );
    }

    @Transactional(readOnly = true)
    public ViewProjectDto getProject(String email, Long projectId) {

        TasklyUser user = projectAccessService.getUserByEmail(email);

        OrgProject project =
                projectAccessService.getProjectForUser(projectId, user);

        return new ViewProjectDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getOrganisation().getName(),
                project.getOrganisation().getCreatedBy().getName(),
                project.getCreatedAt()
        );
    }

    @Transactional
    public List<ProjectMemberListDto> getMembers(String email, Long projectId){
        TasklyUser user = projectAccessService.getUserByEmail(email);

        projectAccessService.getProjectForUser(projectId, user);

        return projectMemberRepository.findProjectMembers(projectId);
    }

    @Transactional
    public UpdateProjectRoleResponseDto projectRoleUpdate(
            String email,
            Long projectId,
            Long userId,
            UpdateProjectRoleRequestDto request) {

        TasklyUser currentUser =
                projectAccessService.getUserByEmail(email);

        OrgProject project =
                projectAccessService.getProjectForUser(projectId, currentUser);

        projectAccessService.verifyOrganisationManager(currentUser);

        ProjectMember targetMembership =
                projectMemberRepository.findByProject_IdAndUser_Id(
                        projectId,
                        userId
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "The specified user is not a member of this project"
                        )
                );

        ProjectRole currentRole = targetMembership.getProjectRole();
        ProjectRole requestedRole = request.role();

        if (currentRole == requestedRole) {
            return new UpdateProjectRoleResponseDto(
                    "User already has the requested project role",
                    projectId,
                    userId,
                    currentRole
            );
        }

        targetMembership.setProjectRole(requestedRole);

        return new UpdateProjectRoleResponseDto(
                "Project member role updated successfully",
                projectId,
                userId,
                requestedRole
        );
    }
}
