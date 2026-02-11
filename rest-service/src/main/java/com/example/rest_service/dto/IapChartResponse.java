package com.example.rest_service.dto;

import java.util.List;

public class IapChartResponse {
    private List<String> labels;
    private List<IapChartSeriesDTO> series;

    public IapChartResponse() {
    }

    public IapChartResponse(List<String> labels, List<IapChartSeriesDTO> series) {
        this.labels = labels;
        this.series = series;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<IapChartSeriesDTO> getSeries() {
        return series;
    }

    public void setSeries(List<IapChartSeriesDTO> series) {
        this.series = series;
    }
}
