package com.example.rest_service.feature.rewardedads.service;

import java.util.List;

import com.example.rest_service.feature.rewardedads.dto.RewardedAmountByDayPlacementDTO;
import com.example.rest_service.feature.rewardedads.dto.RewardedAmountByLevelDTO;
import com.example.rest_service.feature.rewardedads.dto.RewardedAmountByLevelPlacementDTO;
import com.example.rest_service.feature.rewardedads.dto.RewardedAdsFilterOptionsDTO;
import com.example.rest_service.search.SearchFilters;

public interface IRewardedAdsService {

    List<RewardedAmountByDayPlacementDTO> rewardedAmountPerDayGroupedByPlacement(SearchFilters filters);

    List<RewardedAmountByLevelDTO> rewardedAmountPerLevel(SearchFilters filters);

    List<RewardedAmountByLevelPlacementDTO> rewardedAmountPerLevelGroupedByPlacement(SearchFilters filters);

    RewardedAdsFilterOptionsDTO getFilterOptions();

    List<String> getAllCountries();

    List<String> getAllPlacements();

    List<String> getAllGameVersions();

    List<String> getAllPlatforms();

    void initData();
}
