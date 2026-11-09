package com.taskly.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PathsConfig {

    @Bean(name = "publicPaths")
    public List<String> publicPaths(){
        return List.of(
                "/auth/register",
                "/auth/login",
                "/api/swagger-ui.html",
                "/swagger-ui/**",
                "/api/v3/api-docs/**",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/webjars/**"
        );
    }

    @Bean(name = "securedPaths")
    public List<String> securedPaths(){
        return List.of(
                "/api/organisation/register",
                "/api/organisation/join"
        );
    }

    @Bean(name = "managerPaths")
    public List<String> managerPaths() {
        return List.of(
                "/api/project/create",
                "/api/project/*/members/*/role"
        );
    }

    @Bean(name = "employeePaths")
    public List<String> employeePaths(){
        return List.of(
                "/api/project/*/join"
        );
    }

    @Bean(name = "orgMemberPaths")
    public List<String> orgMemberPaths() {
        return List.of(
                "/api/project",
                "/api/project/*",
                "/api/project/*/members",
                "/api/project/{projectId}/task/create",
                "/api/project/{projectId}/tasks/{taskId}/assignees/options",
                "/api/project/{projectId}/tasks/{taskId}/assignees"
        );
    }
}
