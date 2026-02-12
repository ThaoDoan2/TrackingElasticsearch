package com.example.rest_service.dto;

public class IapPlacementRatioDTO {
    private String placement;
    private Double revenue;
    private Double ratio;

    public IapPlacementRatioDTO() {
    }

    public IapPlacementRatioDTO(String placement, Double revenue, Double ratio) {
        this.placement = placement;
        this.revenue = revenue;
        this.ratio = ratio;
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    public Double getRatio() {
        return ratio;
    }

    public void setRatio(Double ratio) {
        this.ratio = ratio;
    }
}
