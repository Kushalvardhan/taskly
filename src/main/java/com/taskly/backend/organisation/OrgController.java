package com.taskly.backend.organisation;

import com.taskly.backend.organisation.dto.CreateOrgRequestDto;
import com.taskly.backend.organisation.dto.CreateOrgResponseDto;
import com.taskly.backend.organisation.dto.JoinOrgRequestDto;
import com.taskly.backend.user.User;
import com.taskly.backend.user.dto.CreateOrgResponseUserDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/organisation")
public class OrgController {

    private final OrgService orgService;

    public OrgController(OrgService orgService) {
        this.orgService = orgService;
    }

    @PostMapping("/register")
    public ResponseEntity<CreateOrgResponseDto> registerOrg(@RequestBody CreateOrgRequestDto orgDto){
        //log.info("received Request Object : {}", orgDto);
        Organisation registeredOrg = orgService.registerOrg(orgDto);

        return ResponseEntity.ok(mapToCreateOrgResponseDto(registeredOrg));
    }

    @PostMapping("/join")
    public ResponseEntity<CreateOrgResponseDto> joinOrg(@RequestBody JoinOrgRequestDto request){
        Organisation joinedOrg = orgService.joinOrg(request);

        return ResponseEntity.ok(mapToCreateOrgResponseDto(joinedOrg));
    }

    private CreateOrgResponseDto mapToCreateOrgResponseDto(Organisation org){

        CreateOrgResponseDto response = new CreateOrgResponseDto();

        response.setId(org.getId());
        response.setName(org.getName());
        response.setCode(org.getCode());
        response.setCreatedAt(org.getCreatedAt());
        response.setUpdatedAt(org.getUpdatedAt());

        User user = org.getCreatedBy();
        CreateOrgResponseUserDto userDto = new CreateOrgResponseUserDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setOrganisationRole(String.valueOf(user.getOrganisationRole()));

        response.setCreatedBy(userDto);
        return response;
    }
}
