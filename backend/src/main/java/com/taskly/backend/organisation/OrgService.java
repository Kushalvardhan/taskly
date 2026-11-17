package com.taskly.backend.organisation;

import com.taskly.backend.access.OrgAccessService;
import com.taskly.backend.access.UserAccessService;
import com.taskly.backend.exception.FieldValidationException;
import com.taskly.backend.organisation.dto.*;
import com.taskly.backend.security.util.JwtUtil;
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
    private final JwtUtil jwtUtil;

    private final UserAccessService userAccessService;
    private final OrgAccessService orgAccessService;

    @Transactional
    public OrgAuthDataDto registerOrg(
            CreateOrgRequestDto orgDto,
            String email
    ) {
        TasklyUser user = userAccessService.getUserByEmail(email);

        orgAccessService.verifyUserHasNoOrganisation(user);

        // Codes are unique; reject duplicates with a field error instead of
        // letting the database constraint fail with a 500.
        if (orgRepository.findByCode(orgDto.getCode()).isPresent()) {
            throw new FieldValidationException(
                    "code",
                    "This code is already in use"
            );
        }

        Organisation org = new Organisation();

        org.setName(orgDto.getName());
        org.setCode(orgDto.getCode());
        org.setCreatedBy(user);

        Organisation savedOrg = orgRepository.save(org);

        user.setOrganisation(savedOrg);
        user.setOrganisationRole(OrganisationRole.ORGANISATION_MANAGER);

        TasklyUser updatedUser = tasklyUserRepository.save(user);

        // Organisation DTO
        OrganisationResponseDto organisationData =
                new OrganisationResponseDto(
                        savedOrg.getId(),
                        savedOrg.getName(),
                        savedOrg.getCode(),
                        savedOrg.getCreatedBy().getId(),
                        savedOrg.getCreatedAt(),
                        savedOrg.getUpdatedAt()
                );

        // Updated user DTO
        OrganisationUserDto userData =
                new OrganisationUserDto(
                        updatedUser.getId(),
                        updatedUser.getName(),
                        updatedUser.getEmail(),
                        updatedUser.getOrganisationRole().name()
                );

        // Generate JWT with updated role
        String newJwtToken = jwtUtil.generateJwtToken(updatedUser);

        return new OrgAuthDataDto(
                organisationData,
                userData,
                newJwtToken
        );
    }

    @Transactional
    public OrgAuthDataDto joinOrg(
            JoinOrgRequestDto request,
            String email
    ) {

        // 1. Find authenticated user
        TasklyUser user = userAccessService.getUserByEmail(email);

        // 2. Verify user has no existing organisation
        orgAccessService.verifyUserHasNoOrganisation(user);

        // 3. Find organisation
        Organisation org =
                orgAccessService.getOrganisationByCode(
                        request.getOrganisationCode()
                );

        // 4. Update user's organisation and role
        user.setOrganisation(org);
        user.setOrganisationRole(OrganisationRole.EMPLOYEE);


        TasklyUser updatedUser = tasklyUserRepository.save(user);

        // Organisation DTO
        OrganisationResponseDto organisationData =
                new OrganisationResponseDto(
                        org.getId(),
                        org.getName(),
                        org.getCode(),
                        org.getCreatedBy().getId(),
                        org.getCreatedAt(),
                        org.getUpdatedAt()
                );

        // Updated user DTO
        OrganisationUserDto userData =
                new OrganisationUserDto(
                        updatedUser.getId(),
                        updatedUser.getName(),
                        updatedUser.getEmail(),
                        updatedUser.getOrganisationRole().name()
                );

        // Generate JWT with updated role
        String newJwtToken = jwtUtil.generateJwtToken(updatedUser);

        return new OrgAuthDataDto(
                organisationData,
                userData,
                newJwtToken
        );
    }
}