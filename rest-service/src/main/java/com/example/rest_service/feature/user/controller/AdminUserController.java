package com.example.rest_service.feature.user.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rest_service.feature.user.dto.CreateUserRequest;
import com.example.rest_service.feature.user.dto.UpdateUserAccessRequest;
import com.example.rest_service.feature.user.dto.UserAccountResponse;
import com.example.rest_service.feature.user.service.UserAccountService;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserAccountService userAccountService;

    public AdminUserController(final UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping
    public List<UserAccountResponse> getUsers() {
        return userAccountService.listUsers();
    }

    @PostMapping
    public UserAccountResponse createUser(@RequestBody final CreateUserRequest request) {
        return userAccountService.createUser(request);
    }

    @PutMapping("/{username}/access")
    public UserAccountResponse updateAccess(
            @PathVariable final String username,
            @RequestBody final UpdateUserAccessRequest request) {
        return userAccountService.updateAccess(username, request);
    }
}
