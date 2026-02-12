package com.example.rest_service.dto;

import java.util.Map;

public class RewardedAmountByLevelPlacementDTO {
    private String level;
    private Map<String, Long> placements;

    public RewardedAmountByLevelPlacementDTO() {
    }

    public RewardedAmountByLevelPlacementDTO(String level, Map<String, Long> placements) {
        this.level = level;
        this.placements = placements;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Map<String, Long> getPlacements() {
        return placements;
    }

    public void setPlacements(Map<String, Long> placements) {
        this.placements = placements;
    }
}
