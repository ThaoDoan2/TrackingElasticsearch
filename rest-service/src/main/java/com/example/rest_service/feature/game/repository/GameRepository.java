package com.example.rest_service.feature.game.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GameRepository extends ElasticsearchRepository<GameDocument, String> {

    Optional<GameDocument> findByGameId(String gameId);

    boolean existsByGameId(String gameId);

    List<GameDocument> findByGameIdIn(List<String> gameIds);
}
