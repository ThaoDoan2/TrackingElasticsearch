package com.example.rest_service.service;

import java.util.List;

import com.example.rest_service.dto.PersonDTO;
import com.example.rest_service.repository.person.PersonDocument;
import com.example.rest_service.search.SearchFilters;

public interface IPersonService {

    public void save(PersonDocument person);

    // public List<PersonDocument> search(SearchFilters filters);

    public List<PersonDTO> search(SearchFilters filters);
}
