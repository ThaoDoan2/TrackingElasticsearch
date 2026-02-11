package com.example.rest_service.service;

import java.util.List;

import com.example.rest_service.dto.IapDTO;
import com.example.rest_service.repository.iap.IapDocument;
import com.example.rest_service.search.SearchFilters;

public interface IIapService {

    void save(IapDocument iap);

    void save(IapDTO iap);

    List<IapDTO> search(SearchFilters filters);
}