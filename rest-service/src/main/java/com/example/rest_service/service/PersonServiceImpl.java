package com.example.rest_service.service;

import java.util.List;

import com.example.rest_service.dto.PersonDTO;
import com.example.rest_service.repository.person.PersonDocument;
import com.example.rest_service.repository.person.PersonRepository;
import com.example.rest_service.search.ElasticsearchProxy;
import com.example.rest_service.search.SearchFilters;
import com.example.rest_service.search.query.QueryType;
import com.example.rest_service.search.query.SearchMeta;
import com.example.rest_service.service.converter.PersonDTOConverter;
import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl implements IPersonService {

    private final PersonRepository repository;
    private final PersonDTOConverter converter;
    private final ElasticsearchProxy<PersonDocument, PersonDTO> client;


    public PersonServiceImpl(PersonRepository repository,
                             PersonDTOConverter converter,
                             ElasticsearchProxy<PersonDocument, PersonDTO> client) {
        this.repository = repository;
        this.converter = converter;
        this.client = client;
    }

    @Override
    public void save(PersonDocument person) {
        repository.save(person);
    }

    @Override
    public void save(PersonDTO person) {
        repository.save(converter.convertToDocument(person));
    }

    @Override
    public List<PersonDTO> search(SearchFilters filters) {
        return client.search(
            filters,
            new SearchMeta(List.of("name"), "person", QueryType.MATCH),
            PersonDocument.class
        );
    }

}
