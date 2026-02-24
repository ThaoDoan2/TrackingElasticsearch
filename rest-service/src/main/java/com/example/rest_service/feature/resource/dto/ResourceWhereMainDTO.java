package com.example.rest_service.feature.resource.dto;

public class ResourceWhereMainDTO {

    private String whereMain;
    private Long amount;
    private Double ratio;

    public ResourceWhereMainDTO(String whereMain, Long amount, Double ratio) {
        this.whereMain = whereMain;
        this.amount = amount;
        this.ratio = ratio;
    }

    public String getWhereMain() {
        return whereMain;
    }

    public void setWhereMain(String whereMain) {
        this.whereMain = whereMain;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Double getRatio() {
        return ratio;
    }

    public void setRatio(Double ratio) {
        this.ratio = ratio;
    }
}
