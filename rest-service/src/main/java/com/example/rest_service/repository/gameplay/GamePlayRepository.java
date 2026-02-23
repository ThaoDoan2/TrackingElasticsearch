package com.example.rest_service.repository.gameplay;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GamePlayRepository extends ElasticsearchRepository<GamePlayDocument, String> {

}
