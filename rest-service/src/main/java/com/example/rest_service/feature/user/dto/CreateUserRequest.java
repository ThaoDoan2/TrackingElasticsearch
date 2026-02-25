package com.example.rest_service.feature.user.dto;

import java.util.List;

public class CreateUserRequest {
    private String username;
    private String password;
    private Boolean passwordEncoded;
    private String role;
    private List<String> gameIds;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getPasswordEncoded() {
        return passwordEncoded;
    }

    public void setPasswordEncoded(Boolean passwordEncoded) {
        this.passwordEncoded = passwordEncoded;
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
}
