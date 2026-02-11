package com.example.rest_service.dto;

import java.util.List;

public class IapChartSeriesDTO {
    private String name;
    private List<Long> data;

    public IapChartSeriesDTO() {
    }

    public IapChartSeriesDTO(String name, List<Long> data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getData() {
        return data;
    }

    public void setData(List<Long> data) {
        this.data = data;
    }
}
