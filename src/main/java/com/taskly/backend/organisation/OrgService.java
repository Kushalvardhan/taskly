package com.taskly.backend.organisation;

import com.taskly.backend.organisation.dto.CreateOrgRequestDto;
import com.taskly.backend.organisation.dto.JoinOrgRequestDto;
import com.taskly.backend.user.User;
import com.taskly.backend.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrgService {

    private final OrgRepository orgRepository;
    private final UserRepository userRepository;

    public OrgService(OrgRepository orgRepository, UserRepository userRepository){
        this.orgRepository = orgRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Organisation registerOrg(CreateOrgRequestDto orgDto){

        //Find user
        User user = userRepository.findById(orgDto.getCreatedByUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        //Create Organisation
        Organisation org = new Organisation();

        org.setName(orgDto.getName());
        org.setCode(orgDto.getCode());
        org.setCreatedBy(user);

        //Attach Organisation to user and give the Organisation manager role
        user.setOrganisation(org);
        user.setOrganisationRole(OrganisationRole.ORGANISATION_MANAGER);

        //Save User and Organisation
        userRepository.save(user);
        return orgRepository.save(org);
    }

    @Transactional
    public Organisation joinOrg(JoinOrgRequestDto request) {

        //Find user
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        //Validate User being part of other organization already.
        if(user.getOrganisation() != null){
            throw new RuntimeException("User already belongs to an organisation!");
        }

        //Find Organisation
        Organisation org = orgRepository.findByCode(request.getOrganisationCode())
                .orElseThrow(() -> new RuntimeException("Organisation not found!"));

        //Attach Organisation info to user and give Employee role.
        user.setOrganisation(org);
        user.setOrganisationRole(OrganisationRole.EMPLOYEE);

        //Save User
        userRepository.save(user);

        return org;
    }
}
