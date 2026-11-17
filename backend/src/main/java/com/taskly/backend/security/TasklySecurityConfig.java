package com.taskly.backend.security;

import com.taskly.backend.organisation.OrganisationRole;
import com.taskly.backend.security.filter.JwtTokenValidatorFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class TasklySecurityConfig {

    private final Environment env;

    @Qualifier("publicPaths")
    private final List<String> publicPaths;

    @Qualifier("securedPaths")
    private final List<String> securedPaths;

    @Qualifier("managerPaths")
    private final List<String> managerPaths;

    @Qualifier("employeePaths")
    private final List<String> employeePaths;

    @Qualifier("orgMemberPaths")
    private final List<String> orgMemberPaths;

    @Bean
    SecurityFilterChain TasklySecurityFilterChain(HttpSecurity http) {
        return http.csrf(csrfConfig -> csrfConfig.disable())
                .cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> {
            publicPaths.forEach(path -> auth.requestMatchers(path).permitAll());

            managerPaths.forEach(path -> auth.requestMatchers(path)
                    .hasRole(OrganisationRole.ORGANISATION_MANAGER.name()));

            employeePaths.forEach(path -> auth.requestMatchers(path)
                    .hasRole(OrganisationRole.EMPLOYEE.name()));

            orgMemberPaths.forEach(path -> auth.requestMatchers(path)
                    .hasAnyRole(
                            OrganisationRole.ORGANISATION_MANAGER.name(),
                            OrganisationRole.EMPLOYEE.name()
                    )
            );

            securedPaths.forEach(path -> auth.requestMatchers(path).authenticated());
            auth.anyRequest().denyAll();
            }
        )
        .addFilterBefore(new JwtTokenValidatorFilter(env, publicPaths), BasicAuthenticationFilter.class)
        .formLogin(flc -> flc.disable())
        .httpBasic(Customizer.withDefaults())
        .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }
}
