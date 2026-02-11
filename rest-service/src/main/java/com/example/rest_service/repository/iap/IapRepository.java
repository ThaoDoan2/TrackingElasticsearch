package com.example.rest_service.repository.iap;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface IapRepository extends ElasticsearchRepository<IapDocument, String> {

}