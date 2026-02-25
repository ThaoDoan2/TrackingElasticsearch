package com.example.rest_service.feature.user.dto;

import java.util.List;

public class LoginResponse {
    private String username;
    private String role;
    private List<String> gameIds;
    private String tokenType;
    private String accessToken;

    public LoginResponse() {
    }

    public LoginResponse(
            String username,
            String role,
            List<String> gameIds,
            String tokenType,
            String accessToken) {
        this.username = username;
        this.role = role;
        this.gameIds = gameIds;
        this.tokenType = tokenType;
        this.accessToken = accessToken;
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

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
