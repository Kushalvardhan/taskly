package com.taskly.backend.access;

import com.taskly.backend.exception.ResourceNotFoundException;
import com.taskly.backend.organisation.Organisation;
import com.taskly.backend.project.OrgProject;
import com.taskly.backend.project.ProjectRepository;
import com.taskly.backend.user.TasklyUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectAccessService {

    private final ProjectRepository projectRepository;
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
}