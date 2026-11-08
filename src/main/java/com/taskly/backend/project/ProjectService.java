package com.taskly.backend.project;

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

    @Transactional(readOnly = true)
    public List<ProjectListDto> getAllProjects(String email) {

        // 1. Identify authenticated user
        TasklyUser user = tasklyUserRepository.findUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        // 2. Verify organisation membership
        if (user.getOrganisation() == null) {
            throw new AccessDeniedException(
                    "You do not belong to any organisation"
            );
        }

        // 3. Get organisation ID
        Long organisationId = user.getOrganisation().getId();

        // 4. Fetch project summaries
        return projectRepository.findProjectSummaries(
                organisationId,
                user.getId()
        );
    }

    //Create Project (Organisation Manager)
    @Transactional
    public OrgProject createProject(CreateProjReqDto request, String email) {

        //find authenticated user
        TasklyUser user = tasklyUserRepository.findUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        //Verify Manager role
        if(user.getOrganisationRole() != OrganisationRole.ORGANISATION_MANAGER) {
            throw new AccessDeniedException(
                    "Only organisation managers can create projects!"
            );
        }

        //verify organisation
        Organisation organisation = user.getOrganisation();

        if(organisation == null){
            throw new AccessDeniedException(
                    "User does not belong to an organisation!"
            );
        }

        //create project
        OrgProject project = new OrgProject();

        project.setName(request.name());
        project.setDescription(request.description());
        project.setOrganisation(organisation);

        OrgProject savedProject = projectRepository.save(project);

        //Add creator as project manager
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
        // 1. Find User
        TasklyUser user = tasklyUserRepository.findUserByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        // 2. Fetch the project
        OrgProject project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found")
                );

        // 3. Verify that the user belongs to an organisation
        if (user.getOrganisation() == null) {
            throw new AccessDeniedException(
                    "You must belong to an organisation to join a project"
            );
        }

        // 4. Verify that the project belongs to the user's organisation
        if (!project.getOrganisation().getId()
                .equals(user.getOrganisation().getId())) {

            throw new AccessDeniedException(
                    "You cannot join a project outside your organisation"
            );
        }

        // 5. Check whether the user is already a project member
        boolean alreadyJoined =
                projectMemberRepository.existsByProjectAndUser(project, user);

        if (alreadyJoined) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You have already joined this project"
            );
        }

        // 6. Create project membership
        ProjectMember projectMember = new ProjectMember();

        projectMember.setProject(project);
        projectMember.setUser(user);
        projectMember.setProjectRole(ProjectRole.MEMBER);

        ProjectMember savedMember = projectMemberRepository.save(projectMember);

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

        // 1. Find the user
        TasklyUser user = tasklyUserRepository.findUserByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        // 2. Verify that the user belongs to an organisation
        if (user.getOrganisation() == null) {
            throw new AccessDeniedException(
                    "You must belong to an organisation to join a project"
            );
        }

        // 3. Fetch the project
        OrgProject project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found")
                );

        // 4. Verify that the project belongs to the user's organisation
        if (!project.getOrganisation().getId()
                .equals(user.getOrganisation().getId())) {

            throw new AccessDeniedException(
                    "You cannot access a project outside your organisation"
            );
        }

        // 6. Prepare DTO
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
        // 1. Find User
        TasklyUser user = tasklyUserRepository.findUserByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        // 2. Fetch the project
        OrgProject project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found")
                );

        // 3. Verify that the user belongs to an organisation
        if (user.getOrganisation() == null) {
            throw new AccessDeniedException(
                    "You must belong to an organisation to join a project"
            );
        }

        // 4. Verify that the project belongs to the user's organisation
        if (!project.getOrganisation().getId()
                .equals(user.getOrganisation().getId())) {

            throw new AccessDeniedException(
                    "You cannot view a project outside your organisation"
            );
        }

        return projectMemberRepository.findProjectMembers(projectId);
    }

    @Transactional
    public UpdateProjectRoleResponseDto projectRoleUpdate(
            String email,
            Long projectId,
            Long userId,
            UpdateProjectRoleRequestDto request) {

        // 1. Find User
        TasklyUser currentUser = tasklyUserRepository.findUserByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        // 2. Fetch the project
        OrgProject project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found")
                );

        // 3. Verify organisation membership
        if (currentUser.getOrganisation() == null ||
                !project.getOrganisation().getId()
                        .equals(currentUser.getOrganisation().getId())) {

            throw new AccessDeniedException(
                    "You cannot manage members of a project outside your organisation"
            );
        }

        // 4. Verify organisation manager role
        if (currentUser.getOrganisationRole()
                != OrganisationRole.ORGANISATION_MANAGER) {

            throw new AccessDeniedException(
                    "Only organisation managers can change project roles"
            );
        }

        // 5. Find the target user's project membership
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

        // 6. Check whether the role actually needs to change
        if (currentRole == requestedRole) {
            return new UpdateProjectRoleResponseDto(
                    "User already has the requested project role",
                    projectId,
                    userId,
                    currentRole
            );
        }

        // 7. Update the role
        targetMembership.setProjectRole(requestedRole);

        return new UpdateProjectRoleResponseDto(
                "Project member role updated successfully",
                projectId,
                userId,
                requestedRole
        );
    }
}
