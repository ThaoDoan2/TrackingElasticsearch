package com.example.rest_service.feature.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminUserBootstrap implements ApplicationRunner {
    private static final Logger LOG = LoggerFactory.getLogger(AdminUserBootstrap.class);

    private final UserAccountService userAccountService;
    private final String adminUsername;
    private final String adminPassword;

    public AdminUserBootstrap(
            final UserAccountService userAccountService,
            @Value("${app.security.admin.username:admin}") final String adminUsername,
            @Value("${app.security.admin.password:admin123}") final String adminPassword) {
        this.userAccountService = userAccountService;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(final ApplicationArguments args) {
        try {
            userAccountService.ensureAdminExists(adminUsername, adminPassword);
        } catch (Exception error) {
            LOG.warn("Skipping admin bootstrap. Root cause: {}", error.getMessage());
        }
    }
}
