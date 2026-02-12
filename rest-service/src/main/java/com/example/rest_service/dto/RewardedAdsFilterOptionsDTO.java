package com.example.rest_service.dto;

import java.util.List;

public class RewardedAdsFilterOptionsDTO {
    private List<String> placements;
    private List<String> gameVersions;
    private List<String> countries;
    private List<String> platforms;

    public RewardedAdsFilterOptionsDTO() {
    }

    public RewardedAdsFilterOptionsDTO(List<String> placements, List<String> gameVersions, List<String> countries,
            List<String> platforms) {
        this.placements = placements;
        this.gameVersions = gameVersions;
        this.countries = countries;
        this.platforms = platforms;
    }

    public List<String> getPlacements() {
        return placements;
    }

    public void setPlacements(List<String> placements) {
        this.placements = placements;
    }

    public List<String> getGameVersions() {
        return gameVersions;
    }

    public void setGameVersions(List<String> gameVersions) {
        this.gameVersions = gameVersions;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
    }
}
