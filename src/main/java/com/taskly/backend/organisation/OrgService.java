package com.taskly.backend.organisation;

import com.taskly.backend.organisation.dto.CreateOrgRequestDto;
import com.taskly.backend.organisation.dto.JoinOrgRequestDto;
import com.taskly.backend.organisation.dto.OrgOperationResult;
import com.taskly.backend.user.TasklyUser;
import com.taskly.backend.user.TasklyUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrgService {

    private final OrgRepository orgRepository;
    private final TasklyUserRepository tasklyUserRepository;

    @Transactional
    public OrgOperationResult registerOrg(CreateOrgRequestDto orgDto, String email) {

        // 1. Find authenticated user
        TasklyUser user = tasklyUserRepository
                .findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Validate existing membership
        if (user.getOrganisation() != null) {
            throw new IllegalStateException(
                    "User already belongs to an organisation"
            );
        }

        // 3. Create organisation
        Organisation org = new Organisation();

        org.setName(orgDto.getName());
        org.setCode(orgDto.getCode());
        org.setCreatedBy(user);

        // 4. Save organisation first
        Organisation savedOrg = orgRepository.save(org);

        // 5. Update user's organisation and role
        user.setOrganisation(savedOrg);
        user.setOrganisationRole(
                OrganisationRole.ORGANISATION_MANAGER
        );

        TasklyUser updatedUser = tasklyUserRepository.save(user);

        return new OrgOperationResult(savedOrg, updatedUser);
    }

    @Transactional
    public OrgOperationResult joinOrg(
            JoinOrgRequestDto request,
            String email
    ) {

        // 1. Find authenticated user
        TasklyUser user = tasklyUserRepository
                .findUserByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // 2. Validate membership
        if (user.getOrganisation() != null) {
            throw new IllegalStateException(
                    "User already belongs to an organisation"
            );
        }

        // 3. Find organisation
        Organisation org = orgRepository
                .findByCode(request.getOrganisationCode())
                .orElseThrow(() ->
                        new RuntimeException("Organisation not found")
                );

        // 4. Update organisation and role
        user.setOrganisation(org);
        user.setOrganisationRole(OrganisationRole.EMPLOYEE);

        TasklyUser updatedUser = tasklyUserRepository.save(user);

        return new OrgOperationResult(org, updatedUser);
    }
}