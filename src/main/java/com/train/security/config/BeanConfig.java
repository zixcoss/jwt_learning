package com.train.security.config;

import com.train.security.config.audit.ApplicationAuditAware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
public class BeanConfig {

    @Bean
    public ApplicationAuditAware auditAware() {
        log.debug("AuditAware initialized.");
        return new ApplicationAuditAware();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        log.debug("PasswordEncoder initialized.");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
        log.debug("AuthenticationManager initialized.");
        return config.getAuthenticationManager();
    }
}
