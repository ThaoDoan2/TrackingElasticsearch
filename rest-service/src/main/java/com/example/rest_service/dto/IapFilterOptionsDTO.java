package com.example.rest_service.dto;

import java.util.List;

public class IapFilterOptionsDTO {
    private List<String> countries;
    private List<String> productIds;
    private List<String> placements;
    private List<String> platforms;
    private List<String> gameVersions;

    public IapFilterOptionsDTO() {
    }

    public IapFilterOptionsDTO(List<String> countries, List<String> productIds, List<String> placements,
            List<String> platforms, List<String> gameVersions) {
        this.countries = countries;
        this.productIds = productIds;
        this.placements = placements;
        this.platforms = platforms;
        this.gameVersions = gameVersions;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }

    public List<String> getPlacements() {
        return placements;
    }

    public void setPlacements(List<String> placements) {
        this.placements = placements;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
    }

    public List<String> getGameVersions() {
        return gameVersions;
    }

    public void setGameVersions(List<String> gameVersions) {
        this.gameVersions = gameVersions;
    }
}
