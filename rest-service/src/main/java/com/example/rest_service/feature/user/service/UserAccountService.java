package com.example.rest_service.feature.user.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpStatus;

import com.example.rest_service.feature.user.dto.ChangePasswordRequest;
import com.example.rest_service.feature.user.dto.CreateUserRequest;
import com.example.rest_service.feature.user.dto.LoginRequest;
import com.example.rest_service.feature.user.dto.LoginResponse;
import com.example.rest_service.feature.user.dto.UpdateUserAccessRequest;
import com.example.rest_service.feature.user.dto.UserAccountResponse;
import com.example.rest_service.feature.user.repository.UserAccountDocument;
import com.example.rest_service.feature.user.repository.UserAccountRepository;
import com.example.rest_service.feature.user.repository.UserRole;
import com.example.rest_service.search.SearchFilters;

@Service
public class UserAccountService implements UserDetailsService {

    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(final UserAccountRepository repository, final PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final UserAccountDocument user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .roles(resolveRole(user).name())
                .disabled(Boolean.FALSE.equals(user.getEnabled()))
                .build();
    }

    public List<UserAccountResponse> listUsers() {
        final List<UserAccountResponse> users = new ArrayList<>();
        for (UserAccountDocument user : repository.findAll()) {
            users.add(toResponse(user));
        }
        users.sort((a, b) -> a.getUsername().compareToIgnoreCase(b.getUsername()));
        return users;
    }

    public UserAccountResponse createUser(final CreateUserRequest request) {
        final String username = normalizeRequiredText(request.getUsername(), "username");
        final String rawPassword = decodePasswordFromClient(
                normalizeRequiredText(request.getPassword(), "password"),
                request.getPasswordEncoded());

        if (repository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("User already exists: " + username);
        }

        final UserRole role = parseRole(request.getRole());
        final UserAccountDocument user = new UserAccountDocument();
        user.setId(username);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(role.name());
        user.setGameIds(normalizeGameIds(request.getGameIds()));
        user.setEnabled(Boolean.TRUE);
        return toResponse(repository.save(user));
    }

    public LoginResponse login(final LoginRequest request) {
        final String username = normalizeRequiredText(request.getUsername(), "username");
        final String password = decodePasswordFromClient(
                normalizeRequiredText(request.getPassword(), "password"),
                request.getPasswordEncoded());

        final UserAccountDocument user = repository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is disabled");
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return new LoginResponse(
                user.getUsername(),
                resolveRole(user).name(),
                normalizeGameIds(user.getGameIds()),
                "SESSION",
                null);
    }

    public void changePassword(final ChangePasswordRequest request) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        final String username = authentication.getName();
        final UserAccountDocument user = repository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is disabled");
        }

        final String oldPassword = decodePasswordFromClient(
                normalizeRequiredText(request.getOldPassword(), "oldPassword"), true);
        final String newPassword = decodePasswordFromClient(
                normalizeRequiredText(request.getNewPassword(), "newPassword"), true);
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect");
        }
        if (oldPassword.equals(newPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be different");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        repository.save(user);
    }

    public UserAccountResponse updateAccess(final String username, final UpdateUserAccessRequest request) {
        final String normalizedUsername = normalizeRequiredText(username, "username");
        final UserAccountDocument user = repository.findByUsername(normalizedUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + normalizedUsername));

        if (request.getGameIds() != null) {
            user.setGameIds(normalizeGameIds(request.getGameIds()));
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }
        return toResponse(repository.save(user));
    }

    public void ensureAdminExists(final String username, final String password) {
        final String normalizedUsername = normalizeRequiredText(username, "admin.username");
        try {
            if (repository.findByUsername(normalizedUsername).isPresent()) {
                return;
            }
        } catch (Exception ignored) {
            // Continue and attempt save. Elasticsearch can auto-create the users index.
        }

        final UserAccountDocument admin = new UserAccountDocument();
        admin.setId(normalizedUsername);
        admin.setUsername(normalizedUsername);
        admin.setPasswordHash(passwordEncoder.encode(normalizeRequiredText(password, "admin.password")));
        admin.setRole(UserRole.ADMIN.name());
        admin.setGameIds(List.of());
        admin.setEnabled(Boolean.TRUE);
        repository.save(admin);
    }

    public void applyGameScope(final SearchFilters filters) {
        if (filters == null) {
            throw new AccessDeniedException("Missing filters");
        }

        final List<String> allowed = resolveAllowedGameIdsForCurrentUser();
        if (allowed.isEmpty()) {
            return;
        }

        final List<String> requested = normalizeGameIds(filters.getGameIds());
        if (requested.isEmpty()) {
            filters.setGameIds(allowed);
            return;
        }

        final Set<String> intersection = requested.stream()
                .filter(allowed::contains)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
        if (intersection.isEmpty()) {
            throw new AccessDeniedException("No access to requested gameIds");
        }
        filters.setGameIds(new ArrayList<>(intersection));
    }

    public List<String> getCurrentUserGameScopeOrEmptyForAdmin() {
        return resolveAllowedGameIdsForCurrentUser();
    }

    private List<String> resolveAllowedGameIdsForCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Unauthorized");
        }
        final boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        if (isAdmin) {
            return List.of();
        }

        final String username = authentication.getName();
        final UserAccountDocument user = repository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("User not found"));
        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new AccessDeniedException("User is disabled");
        }

        final List<String> allowed = normalizeGameIds(user.getGameIds());
        if (allowed.isEmpty()) {
            throw new AccessDeniedException("No game access configured");
        }
        return allowed;
    }

    private static UserAccountResponse toResponse(final UserAccountDocument user) {
        return new UserAccountResponse(
                user.getUsername(),
                resolveRole(user).name(),
                normalizeGameIds(user.getGameIds()),
                !Boolean.FALSE.equals(user.getEnabled()));
    }

    private static UserRole resolveRole(final UserAccountDocument user) {
        return parseRole(user.getRole());
    }

    private static UserRole parseRole(final String roleValue) {
        if (!StringUtils.hasText(roleValue)) {
            return UserRole.USER;
        }
        try {
            return UserRole.valueOf(roleValue.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return UserRole.USER;
        }
    }

    private static List<String> normalizeGameIds(final List<String> gameIds) {
        if (gameIds == null || gameIds.isEmpty()) {
            return List.of();
        }
        return gameIds.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
    }

    private static String normalizeRequiredText(final String value, final String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        final String normalized = value.trim();
        if (!StringUtils.hasText(normalized)) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return normalized;
    }

    private static String decodePasswordFromClient(final String value, final Boolean passwordEncoded) {
        if (!Boolean.TRUE.equals(passwordEncoded)) {
            return value;
        }
        try {
            final byte[] decoded = Base64.getDecoder().decode(value);
            final String plain = new String(decoded, java.nio.charset.StandardCharsets.UTF_8);
            if (!StringUtils.hasText(plain)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Decoded password is empty");
            }
            return plain;
        } catch (IllegalArgumentException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid encoded password");
        }
    }
}
