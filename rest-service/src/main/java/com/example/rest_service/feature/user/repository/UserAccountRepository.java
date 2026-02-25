package com.example.rest_service.feature.user.repository;

import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserAccountRepository extends ElasticsearchRepository<UserAccountDocument, String> {

    Optional<UserAccountDocument> findByUsername(String username);
}
