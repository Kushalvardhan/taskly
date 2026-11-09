package com.taskly.backend.access;

import com.taskly.backend.exception.ResourceNotFoundException;
import com.taskly.backend.organisation.Organisation;
import com.taskly.backend.organisation.OrganisationRole;
import com.taskly.backend.project.OrgProject;
import com.taskly.backend.project.ProjectRepository;
import com.taskly.backend.user.TasklyUser;
import com.taskly.backend.user.TasklyUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectAccessService {

    private final ProjectRepository projectRepository;
    private final TasklyUserRepository tasklyUserRepository;

    // Find authenticated user
    public TasklyUser getUserByEmail(String email) {
        return tasklyUserRepository.findUserByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );
    }

    // Verify that user belongs to an organisation
    public Organisation getUserOrganisation(TasklyUser user) {

        Organisation organisation = user.getOrganisation();

        if (organisation == null) {
            throw new AccessDeniedException(
                    "You do not belong to any organisation"
            );
        }

        return organisation;
    }

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

        Organisation organisation = getUserOrganisation(user);

        OrgProject project = getProjectById(projectId);

        if (!project.getOrganisation().getId()
                .equals(organisation.getId())) {

            throw new AccessDeniedException(
                    "You cannot access a project outside your organisation"
            );
        }

        return project;
    }

    // Verify organisation manager
    public void verifyOrganisationManager(TasklyUser user) {

        if (user.getOrganisationRole()
                != OrganisationRole.ORGANISATION_MANAGER) {

            throw new AccessDeniedException(
                    "Only organisation managers can perform this action"
            );
        }
    }
}