package com.taskly.backend.security.util;

import com.taskly.backend.constants.ApplicationConstants;
import com.taskly.backend.user.TasklyUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final Environment env;

    // Existing login flow
    public String generateJwtToken(Authentication authentication) {

        TasklyUser user =
                (TasklyUser) authentication.getPrincipal();

        String roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return buildToken(user.getEmail(), roles);
    }

    // New method: generate JWT after organisation role changes
    public String generateJwtToken(TasklyUser user) {

        String roles = user.getOrganisationRole() == null
                ? ""
                : "ROLE_" + user.getOrganisationRole().name();

        return buildToken(user.getEmail(), roles);
    }

    // Common JWT generation logic
    private String buildToken(String email, String roles) {

        String secret = env.getProperty(
                ApplicationConstants.JWT_SECRET_KEY,
                ApplicationConstants.JWT_SECRET_DEFAULT_VALUE
        );

        SecretKey secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        Date now = new Date();

        return Jwts.builder()
                .issuer("Taskly")
                .subject(email)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(new Date(
                        now.getTime() + 24L * 60 * 60 * 1000
                ))
                .signWith(secretKey)
                .compact();
    }
}