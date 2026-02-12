package com.example.rest_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rest_service.dto.RewardedAmountByDayPlacementDTO;
import com.example.rest_service.dto.RewardedAmountByLevelDTO;
import com.example.rest_service.dto.RewardedAmountByLevelPlacementDTO;
import com.example.rest_service.search.SearchFilters;
import com.example.rest_service.service.IRewardedAdsService;

@RestController
@RequestMapping("/api/rewarded-ads")
public class RewardedAdsController {

    private final IRewardedAdsService rewardedAdsService;

    public RewardedAdsController(IRewardedAdsService rewardedAdsService) {
        this.rewardedAdsService = rewardedAdsService;
    }

    @PostMapping("/amount-by-date-placement")
    public List<RewardedAmountByDayPlacementDTO> rewardedAmountPerDayGroupedByPlacement(
            @RequestBody final SearchFilters filters) {
        return rewardedAdsService.rewardedAmountPerDayGroupedByPlacement(filters);
    }

    @GetMapping("/amount-by-date-placement")
    public List<RewardedAmountByDayPlacementDTO> rewardedAmountPerDayGroupedByPlacement(
            @RequestParam(required = false) final String term,
            @RequestParam(required = false) final String gameVersion,
            @RequestParam(required = false) final String countryCode,
            @RequestParam(required = false) final String platform,
            @RequestParam(required = false) final String fromDate,
            @RequestParam(required = false) final String toDate) {
        SearchFilters filters = new SearchFilters();
        filters.setTerm(term);
        filters.setGameVersion(gameVersion);
        filters.setCountryCode(countryCode);
        filters.setPlatform(platform);
        filters.setFromDate(fromDate);
        filters.setToDate(toDate);
        return rewardedAdsService.rewardedAmountPerDayGroupedByPlacement(filters);
    }

    @PostMapping("/amount-by-level")
    public List<RewardedAmountByLevelDTO> rewardedAmountPerLevel(@RequestBody final SearchFilters filters) {
        return rewardedAdsService.rewardedAmountPerLevel(filters);
    }

    @GetMapping("/amount-by-level")
    public List<RewardedAmountByLevelDTO> rewardedAmountPerLevel(
            @RequestParam(required = false) final String term,
            @RequestParam(required = false) final String gameVersion,
            @RequestParam(required = false) final String countryCode,
            @RequestParam(required = false) final String platform,
            @RequestParam(required = false) final String fromDate,
            @RequestParam(required = false) final String toDate) {
        SearchFilters filters = new SearchFilters();
        filters.setTerm(term);
        filters.setGameVersion(gameVersion);
        filters.setCountryCode(countryCode);
        filters.setPlatform(platform);
        filters.setFromDate(fromDate);
        filters.setToDate(toDate);
        return rewardedAdsService.rewardedAmountPerLevel(filters);
    }

    @PostMapping("/amount-by-level-placement")
    public List<RewardedAmountByLevelPlacementDTO> rewardedAmountPerLevelGroupedByPlacement(
            @RequestBody final SearchFilters filters) {
        return rewardedAdsService.rewardedAmountPerLevelGroupedByPlacement(filters);
    }

    @GetMapping("/amount-by-level-placement")
    public List<RewardedAmountByLevelPlacementDTO> rewardedAmountPerLevelGroupedByPlacement(
            @RequestParam(required = false) final String term,
            @RequestParam(required = false) final String gameVersion,
            @RequestParam(required = false) final String countryCode,
            @RequestParam(required = false) final String platform,
            @RequestParam(required = false) final String fromDate,
            @RequestParam(required = false) final String toDate) {
        SearchFilters filters = new SearchFilters();
        filters.setTerm(term);
        filters.setGameVersion(gameVersion);
        filters.setCountryCode(countryCode);
        filters.setPlatform(platform);
        filters.setFromDate(fromDate);
        filters.setToDate(toDate);
        return rewardedAdsService.rewardedAmountPerLevelGroupedByPlacement(filters);
    }
}
