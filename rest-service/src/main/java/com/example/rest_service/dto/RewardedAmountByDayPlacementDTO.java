package com.example.rest_service.dto;

import java.util.Map;

public class RewardedAmountByDayPlacementDTO {
    private String date;
    private Map<String, Long> placements;

    public RewardedAmountByDayPlacementDTO() {
    }

    public RewardedAmountByDayPlacementDTO(String date, Map<String, Long> placements) {
        this.date = date;
        this.placements = placements;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, Long> getPlacements() {
        return placements;
    }

    public void setPlacements(Map<String, Long> placements) {
        this.placements = placements;
    }
}
