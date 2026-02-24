package com.example.rest_service.feature.gameplay.dto;

import java.util.Date;

import com.example.rest_service.dto.BaseDTO;

public class GamePlayDTO extends BaseDTO {

    private String userId;
    private String gameId;
    private String eventType;
    private String platform;
    private String country;
    private String gameVersion;
    private Long highestLevel;
    private Long loggedDay;
    private Date accountCreatedDate;
    private Date date;
    private Long duration;
    private String gameMode;
    private Long gameLevel;
    private Long difficulty;
    private String status;
    private Long completion;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }

    public Long getHighestLevel() {
        return highestLevel;
    }

    public void setHighestLevel(Long highestLevel) {
        this.highestLevel = highestLevel;
    }

    public Long getLoggedDay() {
        return loggedDay;
    }

    public void setLoggedDay(Long loggedDay) {
        this.loggedDay = loggedDay;
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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public Long getGameLevel() {
        return gameLevel;
    }

    public void setGameLevel(Long gameLevel) {
        this.gameLevel = gameLevel;
    }

    public Long getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Long difficulty) {
        this.difficulty = difficulty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCompletion() {
        return completion;
    }

    public void setCompletion(Long completion) {
        this.completion = completion;
    }
}
