package com.example.rest_service.repository.resource;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ResourceRepository extends ElasticsearchRepository<ResourceDocument, String> {

}
