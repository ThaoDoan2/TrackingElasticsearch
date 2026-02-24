package com.example.rest_service.feature.iap.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface IapRepository extends ElasticsearchRepository<IapDocument, String> {

}