package com.example.rest_service.service;

import java.util.List;

import com.example.rest_service.dto.RewardedAmountByDayPlacementDTO;
import com.example.rest_service.dto.RewardedAmountByLevelDTO;
import com.example.rest_service.dto.RewardedAmountByLevelPlacementDTO;
import com.example.rest_service.search.SearchFilters;

public interface IRewardedAdsService {

    List<RewardedAmountByDayPlacementDTO> rewardedAmountPerDayGroupedByPlacement(SearchFilters filters);

    List<RewardedAmountByLevelDTO> rewardedAmountPerLevel(SearchFilters filters);

    List<RewardedAmountByLevelPlacementDTO> rewardedAmountPerLevelGroupedByPlacement(SearchFilters filters);
}
