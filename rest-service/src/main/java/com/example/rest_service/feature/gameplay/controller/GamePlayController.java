package com.example.rest_service.feature.gameplay.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rest_service.feature.gameplay.dto.GamePlayDTO;
import com.example.rest_service.feature.gameplay.dto.GamePlayLoseByLevelDTO;
import com.example.rest_service.feature.gameplay.dto.GamePlayStartByLevelDTO;
import com.example.rest_service.feature.gameplay.dto.GamePlayWinByLevelDTO;
import com.example.rest_service.search.SearchFilters;
import com.example.rest_service.feature.gameplay.service.IGamePlayService;

@RestController
@RequestMapping("/api/gameplay")
public class GamePlayController {

    private final IGamePlayService gamePlayService;

    public GamePlayController(IGamePlayService gamePlayService) {
        this.gamePlayService = gamePlayService;
    }

    @PostMapping
    public void save(@RequestBody final GamePlayDTO gameplay) {
        gamePlayService.save(gameplay);
    }

    @PostMapping("/search")
    public List<GamePlayDTO> search(@RequestBody final SearchFilters filters) {
        return gamePlayService.search(filters);
    }

    @GetMapping("/countries")
    public List<String> getAllCountries() {
        return gamePlayService.getAllCountries();
    }

    @GetMapping("/game-versions")
    public List<String> getAllGameVersions() {
        return gamePlayService.getAllGameVersions();
    }

    @GetMapping("/platforms")
    public List<String> getAllPlatforms() {
        return gamePlayService.getAllPlatforms();
    }

    @GetMapping("/search")
    public List<GamePlayDTO> search(
            @RequestParam(required = false) final String term,
            @RequestParam(required = false) final List<String> gameIds,
            @RequestParam(required = false) final List<String> gameVersion,
            @RequestParam(required = false) final List<String> countryCode,
            @RequestParam(required = false) final List<String> platform,
            @RequestParam(required = false) final Integer minLevel,
            @RequestParam(required = false) final Integer maxLevel,
            @RequestParam(required = false) final String fromDate,
            @RequestParam(required = false) final String toDate) {
        SearchFilters filters = new SearchFilters();
        filters.setTerm(term);
        filters.setGameIds(gameIds);
        filters.setGameVersion(gameVersion);
        filters.setCountryCode(countryCode);
        filters.setPlatform(platform);
        filters.setMinLevel(minLevel);
        filters.setMaxLevel(maxLevel);
        filters.setFromDate(fromDate);
        filters.setToDate(toDate);
        return gamePlayService.search(filters);
    }

    @PostMapping("/user-win")
    public List<GamePlayWinByLevelDTO> totalWinsByLevel(@RequestBody final SearchFilters filters) {
        return gamePlayService.totalWinsByLevel(filters);
    }

    @GetMapping("/user-win")
    public List<GamePlayWinByLevelDTO> totalWinsByLevel(
            @RequestParam(required = false) final String term,
            @RequestParam(required = false) final List<String> gameIds,
            @RequestParam(required = false) final List<String> gameVersion,
            @RequestParam(required = false) final List<String> countryCode,
            @RequestParam(required = false) final List<String> platform,
            @RequestParam(required = false) final Integer minLevel,
            @RequestParam(required = false) final Integer maxLevel,
            @RequestParam(required = false) final String fromDate,
            @RequestParam(required = false) final String toDate) {
        SearchFilters filters = new SearchFilters();
        filters.setTerm(term);
        filters.setGameIds(gameIds);
        filters.setGameVersion(gameVersion);
        filters.setCountryCode(countryCode);
        filters.setPlatform(platform);
        filters.setMinLevel(minLevel);
        filters.setMaxLevel(maxLevel);
        filters.setFromDate(fromDate);
        filters.setToDate(toDate);
        return gamePlayService.totalWinsByLevel(filters);
    }

    @PostMapping("/user-start")
    public List<GamePlayStartByLevelDTO> totalStartsByLevel(@RequestBody final SearchFilters filters) {
        return gamePlayService.totalStartsByLevel(filters);
    }

    @GetMapping("/user-start")
    public List<GamePlayStartByLevelDTO> totalStartsByLevel(
            @RequestParam(required = false) final String term,
            @RequestParam(required = false) final List<String> gameIds,
            @RequestParam(required = false) final List<String> gameVersion,
            @RequestParam(required = false) final List<String> countryCode,
            @RequestParam(required = false) final List<String> platform,
            @RequestParam(required = false) final Integer minLevel,
            @RequestParam(required = false) final Integer maxLevel,
            @RequestParam(required = false) final String fromDate,
            @RequestParam(required = false) final String toDate) {
        SearchFilters filters = new SearchFilters();
        filters.setTerm(term);
        filters.setGameIds(gameIds);
        filters.setGameVersion(gameVersion);
        filters.setCountryCode(countryCode);
        filters.setPlatform(platform);
        filters.setMinLevel(minLevel);
        filters.setMaxLevel(maxLevel);
        filters.setFromDate(fromDate);
        filters.setToDate(toDate);
        return gamePlayService.totalStartsByLevel(filters);
    }

    @PostMapping("/user-lose")
    public List<GamePlayLoseByLevelDTO> totalLosesByLevel(@RequestBody final SearchFilters filters) {
        return gamePlayService.totalLosesByLevel(filters);
    }

    @GetMapping("/user-lose")
    public List<GamePlayLoseByLevelDTO> totalLosesByLevel(
            @RequestParam(required = false) final String term,
            @RequestParam(required = false) final List<String> gameIds,
            @RequestParam(required = false) final List<String> gameVersion,
            @RequestParam(required = false) final List<String> countryCode,
            @RequestParam(required = false) final List<String> platform,
            @RequestParam(required = false) final Integer minLevel,
            @RequestParam(required = false) final Integer maxLevel,
            @RequestParam(required = false) final String fromDate,
            @RequestParam(required = false) final String toDate) {
        SearchFilters filters = new SearchFilters();
        filters.setTerm(term);
        filters.setGameIds(gameIds);
        filters.setGameVersion(gameVersion);
        filters.setCountryCode(countryCode);
        filters.setPlatform(platform);
        filters.setMinLevel(minLevel);
        filters.setMaxLevel(maxLevel);
        filters.setFromDate(fromDate);
        filters.setToDate(toDate);
        return gamePlayService.totalLosesByLevel(filters);
    }

    @PostMapping("/generate")
    public void generateSampleData() {
        gamePlayService.initData();
    }
}
