package com.example.rest_service.feature.rewardedads.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface RewardedAdsRepository extends ElasticsearchRepository<RewardedAdsDocument, String> {

}
