package com.example.rest_service.feature.user.controller;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rest_service.feature.user.dto.ChangePasswordRequest;
import com.example.rest_service.feature.user.dto.LoginRequest;
import com.example.rest_service.feature.user.dto.LoginResponse;
import com.example.rest_service.feature.user.service.UserAccountService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserAccountService userAccountService;

    public AuthController(final UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody final LoginRequest request, final HttpServletRequest servletRequest) {
        final LoginResponse response = userAccountService.login(request);

        final UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(
                response.getUsername(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + response.getRole())));
        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        servletRequest.getSession(true)
                .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        return response;
    }

    @PostMapping("/change-password")
    public void changePassword(@RequestBody final ChangePasswordRequest request) {
        userAccountService.changePassword(request);
    }
}
