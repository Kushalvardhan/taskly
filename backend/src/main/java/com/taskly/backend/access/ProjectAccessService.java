package com.taskly.backend.access;

import com.taskly.backend.exception.ResourceNotFoundException;
import com.taskly.backend.organisation.Organisation;
import com.taskly.backend.project.*;
import com.taskly.backend.user.TasklyUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectAccessService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final OrgAccessService orgAccessService;

    // Fetch project
    public OrgProject getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found")
                );
    }

    // Verify that project belongs to user's organisation
    public OrgProject getProjectForUser(
            Long projectId,
            TasklyUser user
    ) {

        Organisation organisation = orgAccessService.getUserOrganisation(user);

        OrgProject project = getProjectById(projectId);

        if (!project.getOrganisation().getId()
                .equals(organisation.getId())) {

            throw new AccessDeniedException(
                    "You cannot access a project outside your organisation"
            );
        }

        return project;
    }

    // Fetch project membership
    public ProjectMember getProjectMembership(
            Long projectId,
            Long userId
    ) {

        return projectMemberRepository
                .findByProject_IdAndUser_Id(projectId, userId)
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "You are not a member of this project"
                        )
                );
    }

    // Verify Project Manager
    public ProjectMember verifyProjectManager(
            Long projectId,
            Long userId
    ) {

        ProjectMember membership =
                getProjectMembership(projectId, userId);

        if (membership.getProjectRole() != ProjectRole.PROJECT_MANAGER) {
            throw new AccessDeniedException(
                    "Only Project Managers can perform this action"
            );
        }

        return membership;
    }
}