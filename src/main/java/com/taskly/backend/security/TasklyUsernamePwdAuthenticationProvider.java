package com.taskly.backend.security;

import com.taskly.backend.user.TasklyUser;
import com.taskly.backend.user.TasklyUserRepository;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TasklyUsernamePwdAuthenticationProvider implements AuthenticationProvider {

    private final TasklyUserRepository tasklyUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();

        Object credentials = authentication.getCredentials();

        if (credentials == null) {
            throw new BadCredentialsException("Password is required");
        }

        String pwd = credentials.toString();

        TasklyUser tasklyUser = tasklyUserRepository.findUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User details not found for the user: " + username));

        List<SimpleGrantedAuthority> authorities =
                tasklyUser.getOrganisationRole() == null
                        ? List.of()
                        : List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + tasklyUser.getOrganisationRole().name())
                        );

        if (passwordEncoder.matches(pwd, tasklyUser.getPasswordHash())) {
            return new UsernamePasswordAuthenticationToken(tasklyUser, null, authorities);
        } else {
            throw new BadCredentialsException("Invalid password!");
        }
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
