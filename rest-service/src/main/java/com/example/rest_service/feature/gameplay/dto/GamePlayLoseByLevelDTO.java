package com.example.rest_service.feature.gameplay.dto;

public class GamePlayLoseByLevelDTO {

    private Long level;
    private Long totalLoses;
    private Long totalUsersLose;
    private Double duration;

    public GamePlayLoseByLevelDTO(Long level, Long totalLoses, Long totalUsersLose, Double duration) {
        this.level = level;
        this.totalLoses = totalLoses;
        this.totalUsersLose = totalUsersLose;
        this.duration = duration;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public Long getTotalLoses() {
        return totalLoses;
    }

    public void setTotalLoses(Long totalLoses) {
        this.totalLoses = totalLoses;
    }

    public Long getTotalUsersLose() {
        return totalUsersLose;
    }

    public void setTotalUsersLose(Long totalUsersLose) {
        this.totalUsersLose = totalUsersLose;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }
}
