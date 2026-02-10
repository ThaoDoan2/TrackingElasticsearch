package com.example.rest_service.search;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.rest_service.dto.PersonDTO;
import com.example.rest_service.repository.person.PersonDocument;
import com.example.rest_service.service.converter.IConverter;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

@Configuration
public class SearchConfiguration {

    @Bean
    public ElasticsearchProxy<PersonDocument, PersonDTO> personElasticsearchProxy(
            ElasticsearchClient client,
            List<IConverter<PersonDocument, PersonDTO>> converters) {
        return new ElasticsearchProxy<>(client, converters);
    }

}
