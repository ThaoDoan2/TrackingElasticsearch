package com.example.rest_service.feature.game.dto;

public class UpdateGameRequest {
    private String name;
    private String apiKey;
    private Boolean enabled;
    private Boolean regenerateApiKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getRegenerateApiKey() {
        return regenerateApiKey;
    }

    public void setRegenerateApiKey(Boolean regenerateApiKey) {
        this.regenerateApiKey = regenerateApiKey;
    }
}
