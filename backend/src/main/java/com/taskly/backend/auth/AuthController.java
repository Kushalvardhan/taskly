package com.taskly.backend.auth;

import com.taskly.backend.common.ApiResponse;
import com.taskly.backend.exception.BadRequestException;
import com.taskly.backend.security.util.JwtUtil;
import com.taskly.backend.user.TasklyUser;
import com.taskly.backend.user.TasklyUserRepository;
import com.taskly.backend.user.dto.CreateUserRequestDto;
import com.taskly.backend.user.dto.LoginRequestDto;
import com.taskly.backend.user.dto.LoginResponseDto;
import com.taskly.backend.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final TasklyUserRepository tasklyUserRepository;
    private final CompromisedPasswordChecker compromisedPasswordChecker;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> apiLogin(
            @RequestBody LoginRequestDto loginRequestDto) {

        var resultAuthentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequestDto.email(),
                                loginRequestDto.password()
                        )
                );

        String jwtToken =
                jwtUtil.generateJwtToken(resultAuthentication);

        var tasklyUser = (TasklyUser) resultAuthentication.getPrincipal();

        UserDto userDto = new UserDto();

        assert tasklyUser != null;
        BeanUtils.copyProperties(tasklyUser, userDto);

        userDto.setOrganisationRole(
                tasklyUser.getOrganisationRole() == null
                        ? null
                        : tasklyUser.getOrganisationRole().name()
        );

        if (tasklyUser.getOrganisation() != null) {
            userDto.setOrganisationId(
                    tasklyUser.getOrganisation().getId()
            );
        }

        LoginResponseDto loginResponse = new LoginResponseDto(
                HttpStatus.OK.getReasonPhrase(),
                userDto,
                jwtToken
        );

        ApiResponse<LoginResponseDto> response = new ApiResponse<>(
                "Login successful",
                HttpStatus.OK.value(),
                Instant.now(),
                loginResponse
        );

        return ResponseEntity.ok(response);
    }


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerUser(
            @RequestBody CreateUserRequestDto createUserRequestDto) {

        CompromisedPasswordDecision decision =
                compromisedPasswordChecker.check(
                        createUserRequestDto.password()
                );

        if (decision.isCompromised()) {
            throw new BadRequestException("Choose a strong password");
        }

        Optional<TasklyUser> existingUser =
                tasklyUserRepository.findUserByEmail(
                        createUserRequestDto.email()
                );

        if (existingUser.isPresent()) {
            throw new BadRequestException("Email is already registered");
        }

        TasklyUser tasklyUser = new TasklyUser();

        BeanUtils.copyProperties(createUserRequestDto, tasklyUser);

        tasklyUser.setPasswordHash(
                passwordEncoder.encode(createUserRequestDto.password())
        );

        tasklyUser.setOrganisation(null);
        tasklyUser.setOrganisationRole(null);

        tasklyUserRepository.save(tasklyUser);

        ApiResponse<String> response = new ApiResponse<>(
                "User registered successfully",
                HttpStatus.CREATED.value(),
                Instant.now(),
                "User registered successfully"
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
