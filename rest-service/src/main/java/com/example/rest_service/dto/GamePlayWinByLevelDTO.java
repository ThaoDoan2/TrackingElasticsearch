package com.example.rest_service.dto;

public class GamePlayWinByLevelDTO {

    private Long level;
    private Long totalWins;
    private Long totalUsersWin;
    private Double duration;

    public GamePlayWinByLevelDTO(Long level, Long totalWins, Long totalUsersWin, Double duration) {
        this.level = level;
        this.totalWins = totalWins;
        this.totalUsersWin = totalUsersWin;
        this.duration = duration;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public Long getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(Long totalWins) {
        this.totalWins = totalWins;
    }

    public Long getTotalUsersWin() {
        return totalUsersWin;
    }

    public void setTotalUsersWin(Long totalUsersWin) {
        this.totalUsersWin = totalUsersWin;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }
}
