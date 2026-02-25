package com.example.rest_service.feature.game.repository;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

import com.example.rest_service.repository.AbstractDocument;

@Document(indexName = GameDocument.INDEX)
@Mapping(mappingPath = "static/games.json")
public class GameDocument extends AbstractDocument {

    public static final String INDEX = "games";

    private String gameId;
    private String name;
    private String apiKey;
    private Boolean enabled;

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
