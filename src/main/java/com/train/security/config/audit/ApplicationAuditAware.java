package com.train.security.config.audit;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

public class ApplicationAuditAware implements AuditorAware<String> {
    @Override
    @Nonnull
    public Optional<String> getCurrentAuditor() {
        try {
            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();
                return Optional.of(request.getRemoteAddr());
            }
            // fix parameter in Optional.ofNullable
            return Optional.of(auth.getName());
        } catch (Exception e) {
            return Optional.of("system");
        }
    }
}
