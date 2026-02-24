package com.example.rest_service.service;

import java.util.List;

import com.example.rest_service.dto.ResourceDTO;
import com.example.rest_service.repository.resource.ResourceDocument;
import com.example.rest_service.search.SearchFilters;

public interface IResourceService {

    void save(ResourceDocument resource);

    void save(ResourceDTO resource);

    List<ResourceDTO> search(SearchFilters filters);

    void initData();
}
