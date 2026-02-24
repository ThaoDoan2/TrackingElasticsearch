package com.example.rest_service.feature.gameplay.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GamePlayRepository extends ElasticsearchRepository<GamePlayDocument, String> {

}
