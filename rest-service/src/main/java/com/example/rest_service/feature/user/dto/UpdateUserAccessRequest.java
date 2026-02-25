package com.example.rest_service.feature.user.dto;

import java.util.List;

public class UpdateUserAccessRequest {
    private List<String> gameIds;
    private Boolean enabled;

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
