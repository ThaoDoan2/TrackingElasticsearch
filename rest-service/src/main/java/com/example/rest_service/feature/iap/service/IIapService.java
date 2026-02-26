package com.example.rest_service.feature.iap.service;

import java.util.List;

import com.example.rest_service.feature.iap.dto.IapCountPerDayDTO;
import com.example.rest_service.feature.iap.dto.IapDailyProductTotalDTO;
import com.example.rest_service.feature.iap.dto.IapDTO;
import com.example.rest_service.feature.iap.dto.IapFilterOptionsDTO;
import com.example.rest_service.feature.iap.dto.IapPlacementRatioDTO;
import com.example.rest_service.feature.iap.repository.IapDocument;
import com.example.rest_service.search.SearchFilters;

public interface IIapService {

    void save(IapDocument iap);

    void save(IapDTO iap);

    List<IapDTO> search(SearchFilters filters);

    List<IapCountPerDayDTO> countPerDay(SearchFilters filters);

    List<IapDailyProductTotalDTO> totalRevenuePerDay(SearchFilters filters);

    List<IapPlacementRatioDTO> purchaseRatioByPlacement(SearchFilters filters);

    IapFilterOptionsDTO getFilterOptions();

    List<String> getAllCountries();

    List<String> getAllProductIds();

    List<String> getAllPlacements();

    List<String> getAllPlatforms();

    List<String> getAllGameVersions();

    void initData();
}
