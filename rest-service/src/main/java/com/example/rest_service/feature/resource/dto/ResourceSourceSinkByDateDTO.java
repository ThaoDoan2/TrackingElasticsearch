package com.example.rest_service.feature.resource.dto;

public class ResourceSourceSinkByDateDTO {

    private String date;
    private Long source;
    private Long sink;

    public ResourceSourceSinkByDateDTO(String date, Long source, Long sink) {
        this.date = date;
        this.source = source;
        this.sink = sink;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    public Long getSink() {
        return sink;
    }

    public void setSink(Long sink) {
        this.sink = sink;
    }
}
