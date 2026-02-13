package com.example.rest_service.search;

import java.util.List;

public class SearchFilters {
    private String term;
    private List<String> gameVersion;
    private List<String> countryCode;
    private List<String> platform;
    private String fromDate;
    private String toDate;
    private List<String> placements;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public List<String> getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(List<String> gameVersion) {
        this.gameVersion = gameVersion;
    }

    public List<String> getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(List<String> countryCode) {
        this.countryCode = countryCode;
    }

    public List<String> getPlatform() {
        return platform;
    }

    public void setPlatform(List<String> platform) {
        this.platform = platform;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public List<String> getPlacements() {
        return placements;
    }

    public void setPlacements(List<String> placements) {
        this.placements = placements;
    }
}
