package com.example.rest_service.repository.person;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

import com.example.rest_service.repository.AbstractDocument;

@Document(indexName = "person")
@Mapping(mappingPath = "static/person.json")
public class PersonDocument extends AbstractDocument {
    private String name;

    public PersonDocument() {}

    public PersonDocument(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
