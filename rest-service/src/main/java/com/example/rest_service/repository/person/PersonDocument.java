package com.example.rest_service.repository.person;

import com.example.rest_service.repository.AbstractDocument;

public class PersonDocument extends AbstractDocument {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
