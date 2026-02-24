package com.example.rest_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rest_service.dto.ResourceDTO;
import com.example.rest_service.search.SearchFilters;
import com.example.rest_service.service.IResourceService;

@RestController
@RequestMapping("/api/resource")
public class ResourceController {

    private final IResourceService resourceService;

    public ResourceController(IResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping
    public void save(@RequestBody final ResourceDTO resource) {
        resourceService.save(resource);
    }

    @PostMapping("/search")
    public List<ResourceDTO> search(@RequestBody final SearchFilters filters) {
        return resourceService.search(filters);
    }

    @GetMapping("/search")
    public List<ResourceDTO> search(
            @RequestParam(required = false) final String term,
            @RequestParam(required = false) final List<String> gameVersion,
            @RequestParam(required = false) final List<String> countryCode,
            @RequestParam(required = false) final List<String> platform,
            @RequestParam(required = false) final Integer minLevel,
            @RequestParam(required = false) final Integer maxLevel,
            @RequestParam(required = false) final String fromDate,
            @RequestParam(required = false) final String toDate) {
        SearchFilters filters = new SearchFilters();
        filters.setTerm(term);
        filters.setGameVersion(gameVersion);
        filters.setCountryCode(countryCode);
        filters.setPlatform(platform);
        filters.setMinLevel(minLevel);
        filters.setMaxLevel(maxLevel);
        filters.setFromDate(fromDate);
        filters.setToDate(toDate);
        return resourceService.search(filters);
    }

    @PostMapping("/generate")
    public void generateData(){
        resourceService.initData();
    }
}
