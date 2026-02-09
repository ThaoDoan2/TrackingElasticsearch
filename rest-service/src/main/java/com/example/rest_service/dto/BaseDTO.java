package com.example.rest_service.dto;

public abstract class BaseDTO {
    private String id;

    public String getId() {
        return id;
    }   

    public void setId(String id) {
        this.id = id;
    }
}
