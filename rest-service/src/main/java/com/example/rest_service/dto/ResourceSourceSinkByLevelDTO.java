package com.example.rest_service.dto;

public class ResourceSourceSinkByLevelDTO {

    private Long level;
    private Long source;
    private Long sink;

    public ResourceSourceSinkByLevelDTO(Long level, Long source, Long sink) {
        this.level = level;
        this.source = source;
        this.sink = sink;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
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
