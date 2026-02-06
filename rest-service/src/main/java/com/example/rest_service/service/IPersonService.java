package com.example.rest_service.service;

import java.util.List;

import com.example.rest_service.repository.person.PersonDocument;

public interface IPersonService {

    public void save(PersonDocument person);

    public List<PersonDocument> search(SearchFilters filters);
}
