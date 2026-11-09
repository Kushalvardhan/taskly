package com.taskly.backend.access;

import com.taskly.backend.exception.ResourceNotFoundException;
import com.taskly.backend.organisation.Organisation;
import com.taskly.backend.organisation.OrgRepository;
import com.taskly.backend.organisation.OrganisationRole;
import com.taskly.backend.user.TasklyUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrgAccessService {

    private final OrgRepository orgRepository;

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

    // Verify that user does not belong to an organisation
    public void verifyUserHasNoOrganisation(TasklyUser user) {

        if (user.getOrganisation() != null) {
            throw new AccessDeniedException(
                    "You already belong to an organisation"
            );
        }
    }

    // Fetch organisation by ID
    public Organisation getOrganisationById(Long organisationId) {

        return orgRepository.findById(organisationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Organisation not found")
                );
    }

    // Fetch organisation by code
    public Organisation getOrganisationByCode(String code) {

        return orgRepository.findByCode(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Organisation not found")
                );
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