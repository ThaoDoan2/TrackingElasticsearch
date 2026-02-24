package com.example.rest_service.feature.resource.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rest_service.feature.resource.dto.ResourceDTO;
import com.example.rest_service.feature.resource.dto.ResourceSourceSinkByDateDTO;
import com.example.rest_service.feature.resource.dto.ResourceSourceSinkByLevelDTO;
import com.example.rest_service.feature.resource.dto.ResourceWhereMainDTO;
import com.example.rest_service.search.SearchFilters;
import com.example.rest_service.feature.resource.service.IResourceService;

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

    @GetMapping("/countries")
    public List<String> getAllCountries() {
        return resourceService.getAllCountries();
    }

    @GetMapping("/game-versions")
    public List<String> getAllGameVersions() {
        return resourceService.getAllGameVersions();
    }

    @GetMapping("/platforms")
    public List<String> getAllPlatforms() {
        return resourceService.getAllPlatforms();
    }

    @GetMapping("/placements")
    public List<String> getAllPlacements() {
        return resourceService.getAllPlacements();
    }

    @GetMapping("/sub-placements")
    public List<String> getAllSubPlacements() {
        return resourceService.getAllSubPlacements();
    }

    @GetMapping("/item-names")
    public List<String> getAllItemNames() {
        return resourceService.getAllItemNames();
    }

    @GetMapping("/search")
    public List<ResourceDTO> search(
            @RequestParam(required = false) final String term,
            @RequestParam(required = false) final List<String> gameVersion,
            @RequestParam(required = false) final List<String> countryCode,
            @RequestParam(required = false) final List<String> platform,
            @RequestParam(required = false) final List<String> placements,
            @RequestParam(required = false) final List<String> subPlacements,
            @RequestParam(required = false) final List<String> itemNames,
            @RequestParam(required = false) final Integer minLevel,
            @RequestParam(required = false) final Integer maxLevel,
            @RequestParam(required = false) final String fromDate,
            @RequestParam(required = false) final String toDate) {
        return resourceService.search(buildFilters(
                term, gameVersion, countryCode, platform, placements, subPlacements, itemNames,
                minLevel, maxLevel, fromDate, toDate));
    }

    @PostMapping("/source-sink-by-date")
    public List<ResourceSourceSinkByDateDTO> sourceSinkByDate(@RequestBody final SearchFilters filters) {
        return resourceService.sourceSinkByDate(filters);
    }

    @GetMapping("/source-sink-by-date")
    public List<ResourceSourceSinkByDateDTO> sourceSinkByDate(
            @RequestParam(required = false) final String term,
            @RequestParam(required = false) final List<String> gameVersion,
            @RequestParam(required = false) final List<String> countryCode,
            @RequestParam(required = false) final List<String> platform,
            @RequestParam(required = false) final List<String> placements,
            @RequestParam(required = false) final List<String> subPlacements,
            @RequestParam(required = false) final List<String> itemNames,
            @RequestParam(required = false) final Integer minLevel,
            @RequestParam(required = false) final Integer maxLevel,
            @RequestParam(required = false) final String fromDate,
            @RequestParam(required = false) final String toDate) {
        return resourceService.sourceSinkByDate(buildFilters(
                term, gameVersion, countryCode, platform, placements, subPlacements, itemNames,
                minLevel, maxLevel, fromDate, toDate));
    }

    @PostMapping("/source-sink-by-level")
    public List<ResourceSourceSinkByLevelDTO> sourceSinkByLevel(@RequestBody final SearchFilters filters) {
        return resourceService.sourceSinkByLevel(filters);
    }

    @GetMapping("/source-sink-by-level")
    public List<ResourceSourceSinkByLevelDTO> sourceSinkByLevel(
            @RequestParam(required = false) final String term,
            @RequestParam(required = false) final List<String> gameVersion,
            @RequestParam(required = false) final List<String> countryCode,
            @RequestParam(required = false) final List<String> platform,
            @RequestParam(required = false) final List<String> placements,
            @RequestParam(required = false) final List<String> subPlacements,
            @RequestParam(required = false) final List<String> itemNames,
            @RequestParam(required = false) final Integer minLevel,
            @RequestParam(required = false) final Integer maxLevel,
            @RequestParam(required = false) final String fromDate,
            @RequestParam(required = false) final String toDate) {
        return resourceService.sourceSinkByLevel(buildFilters(
                term, gameVersion, countryCode, platform, placements, subPlacements, itemNames,
                minLevel, maxLevel, fromDate, toDate));
    }

    @PostMapping("/source-by-where-main")
    public List<ResourceWhereMainDTO> sourceByWhereMain(@RequestBody final SearchFilters filters) {
        return resourceService.sourceByWhereMain(filters);
    }

    @GetMapping("/source-by-where-main")
    public List<ResourceWhereMainDTO> sourceByWhereMain(
            @RequestParam(required = false) final String term,
            @RequestParam(required = false) final List<String> gameVersion,
            @RequestParam(required = false) final List<String> countryCode,
            @RequestParam(required = false) final List<String> platform,
            @RequestParam(required = false) final List<String> placements,
            @RequestParam(required = false) final List<String> subPlacements,
            @RequestParam(required = false) final List<String> itemNames,
            @RequestParam(required = false) final Integer minLevel,
            @RequestParam(required = false) final Integer maxLevel,
            @RequestParam(required = false) final String fromDate,
            @RequestParam(required = false) final String toDate) {
        return resourceService.sourceByWhereMain(buildFilters(
                term, gameVersion, countryCode, platform, placements, subPlacements, itemNames,
                minLevel, maxLevel, fromDate, toDate));
    }

    @PostMapping("/sink-by-where-main")
    public List<ResourceWhereMainDTO> sinkByWhereMain(@RequestBody final SearchFilters filters) {
        return resourceService.sinkByWhereMain(filters);
    }

    @GetMapping("/sink-by-where-main")
    public List<ResourceWhereMainDTO> sinkByWhereMain(
            @RequestParam(required = false) final String term,
            @RequestParam(required = false) final List<String> gameVersion,
            @RequestParam(required = false) final List<String> countryCode,
            @RequestParam(required = false) final List<String> platform,
            @RequestParam(required = false) final List<String> placements,
            @RequestParam(required = false) final List<String> subPlacements,
            @RequestParam(required = false) final List<String> itemNames,
            @RequestParam(required = false) final Integer minLevel,
            @RequestParam(required = false) final Integer maxLevel,
            @RequestParam(required = false) final String fromDate,
            @RequestParam(required = false) final String toDate) {
        return resourceService.sinkByWhereMain(buildFilters(
                term, gameVersion, countryCode, platform, placements, subPlacements, itemNames,
                minLevel, maxLevel, fromDate, toDate));
    }

    private static SearchFilters buildFilters(
            final String term,
            final List<String> gameVersion,
            final List<String> countryCode,
            final List<String> platform,
            final List<String> placements,
            final List<String> subPlacements,
            final List<String> itemNames,
            final Integer minLevel,
            final Integer maxLevel,
            final String fromDate,
            final String toDate) {
        SearchFilters filters = new SearchFilters();
        filters.setTerm(term);
        filters.setGameVersion(gameVersion);
        filters.setCountryCode(countryCode);
        filters.setPlatform(platform);
        filters.setPlacements(placements);
        filters.setSubPlacements(subPlacements);
        filters.setItemNames(itemNames);
        filters.setMinLevel(minLevel);
        filters.setMaxLevel(maxLevel);
        filters.setFromDate(fromDate);
        filters.setToDate(toDate);
        return filters;
    }

    @PostMapping("/generate")
    public void generateData(){
        resourceService.initData();
    }
}
