package com.example.rest_service.repository.resource;

import java.util.Date;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

import com.example.rest_service.repository.AbstractDocument;

@Document(indexName = "resource")
@Mapping(mappingPath = "static/resource.json")
public class ResourceDocument extends AbstractDocument {
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

    private String placement;
    private String subPlacement;
    private int itemId;
    private String itemName;
    private Long amount;

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

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
