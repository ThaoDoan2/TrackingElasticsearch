package com.example.rest_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.rest_service.dto.IapDTO;
import com.example.rest_service.repository.iap.IapDocument;
import com.example.rest_service.repository.iap.IapRepository;
import com.example.rest_service.search.ElasticsearchProxy;
import com.example.rest_service.search.SearchFilters;
import com.example.rest_service.search.query.QueryType;
import com.example.rest_service.search.query.SearchMeta;
import com.example.rest_service.service.converter.IapDTOConverter;

@Service
public class IapServiceImpl implements IIapService {

    private final IapRepository repository;
    private final IapDTOConverter converter;
    private final ElasticsearchProxy<IapDocument, IapDTO> client;

    public IapServiceImpl(IapRepository repository,
            IapDTOConverter converter,
            ElasticsearchProxy<IapDocument, IapDTO> client) {
        this.repository = repository;
        this.converter = converter;
        this.client = client;
    }

    @Override
    public void save(IapDocument iap) {
        repository.save(iap);
    }

    @Override
    public void save(IapDTO iap) {
        repository.save(converter.convertToDocument(iap));
    }

    @Override
    public List<IapDTO> search(SearchFilters filters) {
        return client.search(
                filters,
                new SearchMeta(List.of("userId", "productId", "transactionId"), "iap", QueryType.MATCH),
                IapDocument.class);
    }
}