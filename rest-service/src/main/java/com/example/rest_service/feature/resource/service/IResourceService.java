package com.example.rest_service.feature.resource.service;

import java.util.List;

import com.example.rest_service.feature.resource.dto.ResourceDTO;
import com.example.rest_service.feature.resource.dto.ResourceSourceSinkByDateDTO;
import com.example.rest_service.feature.resource.dto.ResourceSourceSinkByLevelDTO;
import com.example.rest_service.feature.resource.dto.ResourceWhereMainDTO;
import com.example.rest_service.feature.resource.repository.ResourceDocument;
import com.example.rest_service.search.SearchFilters;

public interface IResourceService {

    void save(ResourceDocument resource);

    void save(ResourceDTO resource);

    List<ResourceDTO> search(SearchFilters filters);

    List<ResourceSourceSinkByDateDTO> sourceSinkByDate(SearchFilters filters);

    List<ResourceSourceSinkByLevelDTO> sourceSinkByLevel(SearchFilters filters);

    List<ResourceWhereMainDTO> sourceByWhereMain(SearchFilters filters);

    List<ResourceWhereMainDTO> sinkByWhereMain(SearchFilters filters);

    List<String> getAllPlatforms();

    List<String> getAllPlacements();

    List<String> getAllSubPlacements();

    List<String> getAllCountries();

    List<String> getAllGameVersions();

    List<String> getAllItemNames();

    void initData();
}
