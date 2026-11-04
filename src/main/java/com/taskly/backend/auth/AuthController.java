package com.taskly.backend.auth;

import com.taskly.backend.constants.ApplicationConstants;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final TasklyUserRepository tasklyUserRepository;
    private final CompromisedPasswordChecker compromisedPasswordChecker;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> apiLogin(
            @RequestBody LoginRequestDto loginRequestDto) {

        log.info("STEP 1: Login endpoint reached");

        try {
            log.info("STEP 2: Calling AuthenticationManager");

            var resultAuthentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequestDto.email(),
                                    loginRequestDto.password()
                            )
                    );

            log.info("STEP 3: Authentication successful");

            log.info("STEP 4: Generating JWT");

            String jwtToken =
                    jwtUtil.generateJwtToken(resultAuthentication);

            log.info("STEP 5: JWT generated successfully");

            var tasklyUser = (TasklyUser) resultAuthentication.getPrincipal();

            assert tasklyUser != null;
            UserDto userDto = new UserDto();

            BeanUtils.copyProperties(tasklyUser, userDto);

            // Explicitly convert enum to String
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

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new LoginResponseDto(
                            HttpStatus.OK.getReasonPhrase(),
                            userDto,
                            jwtToken
                    ));

        } catch (BadCredentialsException ex) {

            log.warn("LOGIN FAILED: Bad credentials", ex);

            return buildErrorResponse(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password"
            );

        } catch (AuthenticationException ex) {

            log.error("LOGIN FAILED: Authentication exception", ex);

            return buildErrorResponse(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication failed"
            );

        } catch (Exception ex) {

            log.error("LOGIN FAILED: Unexpected exception", ex);

            return buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred"
            );
        }
    }


    @PostMapping(value = "/register")
    public ResponseEntity<?> registerUser(@RequestBody CreateUserRequestDto createUserRequestDto) {
        CompromisedPasswordDecision decision = compromisedPasswordChecker
                .check(createUserRequestDto.password());
        if (decision.isCompromised()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("password", "Choose a strong password"));
        }
        Optional<TasklyUser> existingUser = tasklyUserRepository.findUserByEmail(createUserRequestDto.email());
        if (existingUser.isPresent()) {
            Map<String, String> errors = new HashMap<>();
            TasklyUser tasklyUser = existingUser.get();
            if (tasklyUser.getEmail().equalsIgnoreCase(createUserRequestDto.email())) {
                errors.put("email", "Email is already registered");
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        TasklyUser tasklyUser = new TasklyUser();
        BeanUtils.copyProperties(createUserRequestDto, tasklyUser);
        tasklyUser.setPasswordHash(passwordEncoder.encode(createUserRequestDto.password()));
        tasklyUser.setOrganisation(null);
        tasklyUser.setOrganisationRole(null);
        tasklyUserRepository.save(tasklyUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

    private ResponseEntity<LoginResponseDto> buildErrorResponse(HttpStatus status,
                                                                String message) {
        return ResponseEntity
                .status(status)
                .body(new LoginResponseDto(message, null, null));
    }

}
