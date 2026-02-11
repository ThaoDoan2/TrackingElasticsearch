package com.example.rest_service.dto;

import java.util.Map;

public class IapChartCompactRowDTO {
    private String date;
    private Map<String, Long> products;

    public IapChartCompactRowDTO() {
    }

    public IapChartCompactRowDTO(String date, Map<String, Long> products) {
        this.date = date;
        this.products = products;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, Long> getProducts() {
        return products;
    }

    public void setProducts(Map<String, Long> products) {
        this.products = products;
    }
}
