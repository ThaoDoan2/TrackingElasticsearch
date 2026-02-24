package com.example.rest_service.feature.gameplay.dto;

public class GamePlayStartByLevelDTO {

    private Long level;
    private Long totalStarts;
    private Long totalUsersStart;

    public GamePlayStartByLevelDTO(Long level, Long totalStarts, Long totalUsersStart) {
        this.level = level;
        this.totalStarts = totalStarts;
        this.totalUsersStart = totalUsersStart;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public Long getTotalStarts() {
        return totalStarts;
    }

    public void setTotalStarts(Long totalStarts) {
        this.totalStarts = totalStarts;
    }

    public Long getTotalUsersStart() {
        return totalUsersStart;
    }

    public void setTotalUsersStart(Long totalUsersStart) {
        this.totalUsersStart = totalUsersStart;
    }
}
