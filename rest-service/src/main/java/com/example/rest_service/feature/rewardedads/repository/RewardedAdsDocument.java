package com.example.rest_service.feature.rewardedads.repository;

import java.util.Date;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

import com.example.rest_service.repository.AbstractDocument;

@Document(indexName = RewardedAdsDocument.INDEX)
@Mapping(mappingPath = "static/rewarded_ads.json")
public class RewardedAdsDocument extends AbstractDocument {

    public static final String INDEX = "rewarded_ads";

    private String userId;
    private String gameId;
    private String eventType;
    private String country;
    
    private String placement;
    private String subPlacement;
    private String platform;
    private String gameVersion;
    private Long level;
    private Long loggedDay;
    private Long highestLevel;
    private Date accountCreatedDate;
    private Date date;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public String getSubPlacement() {
        return subPlacement;
    }

    public void setSubPlacement(String subPlacement) {
        this.subPlacement = subPlacement;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public Long getLoggedDay() {
        return loggedDay;
    }

    public void setLoggedDay(Long loggedDay) {
        this.loggedDay = loggedDay;
    }

    public Long getHighestLevel() {
        return highestLevel;
    }

    public void setHighestLevel(Long highestLevel) {
        this.highestLevel = highestLevel;
    }

    public Date getAccountCreatedDate() {
        return accountCreatedDate;
    }

    public void setAccountCreatedDate(Date accountCreatedDate) {
        this.accountCreatedDate = accountCreatedDate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
