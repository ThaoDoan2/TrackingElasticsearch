package com.example.rest_service.service;

import java.util.List;

import com.example.rest_service.dto.IapChartCompactRowDTO;
import com.example.rest_service.dto.IapChartResponse;
import com.example.rest_service.dto.IapDailyProductTotalDTO;
import com.example.rest_service.dto.IapDTO;
import com.example.rest_service.repository.iap.IapDocument;
import com.example.rest_service.search.SearchFilters;

public interface IIapService {

    void save(IapDocument iap);

    void save(IapDTO iap);

    List<IapDTO> search(SearchFilters filters);

    IapChartResponse chart(SearchFilters filters);

    List<IapChartCompactRowDTO> chartCompact(SearchFilters filters);

    List<IapDailyProductTotalDTO> totalPurchasePerDay(SearchFilters filters);
}
