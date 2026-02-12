package com.example.rest_service.dto;

public class RewardedAmountByLevelDTO {
    private String level;
    private Long amount;

    public RewardedAmountByLevelDTO() {
    }

    public RewardedAmountByLevelDTO(String level, Long amount) {
        this.level = level;
        this.amount = amount;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
