package com.example.rest_service.dto;

import java.util.Map;

public class IapDailyProductTotalDTO {
    private String date;
    private Map<String, Double> products;

    public IapDailyProductTotalDTO() {
    }

    public IapDailyProductTotalDTO(String date, Map<String, Double> products) {
        this.date = date;
        this.products = products;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, Double> getProducts() {
        return products;
    }

    public void setProducts(Map<String, Double> products) {
        this.products = products;
    }
}
