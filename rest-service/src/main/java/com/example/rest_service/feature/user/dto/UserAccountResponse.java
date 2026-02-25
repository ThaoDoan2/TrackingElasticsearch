package com.example.rest_service.feature.user.dto;

import java.util.List;

public class UserAccountResponse {
    private String username;
    private String role;
    private List<String> gameIds;
    private Boolean enabled;

    public UserAccountResponse() {
    }

    public UserAccountResponse(String username, String role, List<String> gameIds, Boolean enabled) {
        this.username = username;
        this.role = role;
        this.gameIds = gameIds;
        this.enabled = enabled;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getGameIds() {
        return gameIds;
    }

    public void setGameIds(List<String> gameIds) {
        this.gameIds = gameIds;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
