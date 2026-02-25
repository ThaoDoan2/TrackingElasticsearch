package com.example.rest_service.feature.game.dto;

public class GameResponse {
    private String gameId;
    private String name;
    private String apiKey;
    private Boolean enabled;

    public GameResponse() {
    }

    public GameResponse(String gameId, String name, String apiKey, Boolean enabled) {
        this.gameId = gameId;
        this.name = name;
        this.apiKey = apiKey;
        this.enabled = enabled;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

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
}
