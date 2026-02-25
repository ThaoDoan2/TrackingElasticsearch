package com.example.rest_service.feature.iap.service;

import java.util.List;

import com.example.rest_service.feature.iap.dto.IapChartCompactRowDTO;
import com.example.rest_service.feature.iap.dto.IapChartResponse;
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

    IapChartResponse chart(SearchFilters filters);

    List<IapChartCompactRowDTO> chartCompact(SearchFilters filters);

    List<IapDailyProductTotalDTO> totalPurchasePerDay(SearchFilters filters);

    List<IapPlacementRatioDTO> purchaseRatioByPlacement(SearchFilters filters);

    IapFilterOptionsDTO getFilterOptions();

    List<String> getAllCountries();

    List<String> getAllProductIds();

    List<String> getAllPlacements();

    List<String> getAllPlatforms();

    List<String> getAllGameVersions();

    void initData();
}
