package br.edu.utfpr.pb.pw45s.server.audit;

import br.edu.utfpr.pb.pw45s.server.model.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<User> {

    @SuppressWarnings("NullableProblems")
    @Override
    public Optional<User> getCurrentAuditor() {
        var authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        User user = (User) authentication.getPrincipal(); // ou outro identificador único
        return Optional.ofNullable(user);
    }
}

