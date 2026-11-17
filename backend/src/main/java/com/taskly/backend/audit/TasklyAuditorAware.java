
package com.taskly.backend.audit;

import com.taskly.backend.user.TasklyUser;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("tasklyAuditorAware")
@NullMarked
public class TasklyAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {

            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof TasklyUser user) {
            return Optional.of(user.getEmail());
        }

        if (principal instanceof String email && !email.isBlank()) {
            return Optional.of(email);
        }

        return Optional.empty();
    }
}
