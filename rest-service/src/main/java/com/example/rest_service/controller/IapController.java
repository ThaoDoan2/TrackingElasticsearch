package com.example.rest_service.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rest_service.dto.IapChartCompactRowDTO;
import com.example.rest_service.dto.IapChartResponse;
import com.example.rest_service.dto.IapDTO;
import com.example.rest_service.dto.IapDailyProductTotalDTO;
import com.example.rest_service.dto.IapPlacementRatioDTO;
import com.example.rest_service.search.SearchFilters;
import com.example.rest_service.service.IIapService;

@RestController
@RequestMapping("/api/iap")
public class IapController {

    private final IIapService iapService;

    private static final Logger LOG = LoggerFactory.getLogger(IapController.class);

    public IapController(IIapService iapService) {
        this.iapService = iapService;
    }

    @PostMapping
    public void save(@RequestBody final IapDTO iap) {
        iapService.save(iap);
    }

    @GetMapping("/platforms")
    public List<String> getAllPlatforms() {
        return iapService.getAllPlatforms();
    }

    @PostMapping("/search")
    public List<IapDTO> search(@RequestBody final SearchFilters filters) {
        return iapService.search(filters);
    }

    @GetMapping("/search")
    public List<IapDTO> search(
            @RequestParam(required = false) final String term,
            @RequestParam(required = false) final List<String> gameVersion,
            @RequestParam(required = false) final List<String> countryCode,
            @RequestParam(required = false) final List<String> platform,
            @RequestParam(required = false) final String fromDate,
            @RequestParam(required = false) final String toDate) {
        SearchFilters filters = new SearchFilters();
        filters.setTerm(term);
        filters.setGameVersion(gameVersion);
        filters.setCountryCode(countryCode);
        filters.setPlatform(platform);
        filters.setFromDate(fromDate);
        filters.setToDate(toDate);
        return iapService.search(filters);
    }

    @PostMapping("/chart")
    public IapChartResponse chart(@RequestBody final SearchFilters filters) {
        return iapService.chart(filters);
    }

    @GetMapping("/chart")
    public IapChartResponse chart(
            @RequestParam(required = false) final String term,
            @RequestParam(required = false) final List<String> gameVersion,
            @RequestParam(required = false) final List<String> countryCode,
            @RequestParam(required = false) final List<String> platform,
            @RequestParam(required = false) final String fromDate,
            @RequestParam(required = false) final String toDate) {
        SearchFilters filters = new SearchFilters();
        filters.setTerm(term);
        filters.setGameVersion(gameVersion);
        filters.setCountryCode(countryCode);
        filters.setPlatform(platform);
        filters.setFromDate(fromDate);
        filters.setToDate(toDate);
        return iapService.chart(filters);
    }

    @PostMapping("/chart/compact")
    public List<IapChartCompactRowDTO> chartCompact(@RequestBody final SearchFilters filters) {
        return iapService.chartCompact(filters);
    }

    @GetMapping("/chart/compact")
    public List<IapChartCompactRowDTO> chartCompact(
            @RequestParam(required = false) final String term,
            @RequestParam(required = false) final List<String> gameVersion,
            @RequestParam(required = false) final List<String> countryCode,
            @RequestParam(required = false) final List<String> platform,
            @RequestParam(required = false) final String fromDate,
            @RequestParam(required = false) final String toDate,
            @RequestParam(required = false) final String[] products) {
                LOG.info("Received request for chartCompact with term={}, gameVersion={}, countryCode={}, platform={}, fromDate={}, toDate={}", term, gameVersion, countryCode, platform, fromDate, toDate);
        SearchFilters filters = new SearchFilters();
        filters.setTerm(term);
        filters.setGameVersion(gameVersion);
        filters.setCountryCode(countryCode);
        filters.setPlatform(platform);
        filters.setFromDate(fromDate);
        filters.setToDate(toDate);
        return iapService.chartCompact(filters);
    }

    @PostMapping("/revenue-by-date")
    public List<IapDailyProductTotalDTO> totalPurchasePerDay(@RequestBody final SearchFilters filters) {
        return iapService.totalPurchasePerDay(filters);
    }

    @GetMapping("/revenue-by-date")
    public List<IapDailyProductTotalDTO> totalPurchasePerDay(
            @RequestParam(required = false) final String term,
            @RequestParam(required = false) final List<String> gameVersion,
            @RequestParam(required = false) final List<String> countryCode,
            @RequestParam(required = false) final List<String> platform,
            @RequestParam(required = false) final String fromDate,
            @RequestParam(required = false) final String toDate) {
        SearchFilters filters = new SearchFilters();
        filters.setTerm(term);
        filters.setGameVersion(gameVersion);
        filters.setCountryCode(countryCode);
        filters.setPlatform(platform);
        filters.setFromDate(fromDate);
        filters.setToDate(toDate);
        return iapService.totalPurchasePerDay(filters);
    }

    @PostMapping("/ratio/placement")
    public List<IapPlacementRatioDTO> purchaseRatioByPlacement(@RequestBody final SearchFilters filters) {
        return iapService.purchaseRatioByPlacement(filters);
    }

    @GetMapping("/ratio/placement")
    public List<IapPlacementRatioDTO> purchaseRatioByPlacement(
            @RequestParam(required = false) final String term,
            @RequestParam(required = false) final List<String> gameVersion,
            @RequestParam(required = false) final List<String> countryCode,
            @RequestParam(required = false) final List<String> platform,
            @RequestParam(required = false) final String fromDate,
            @RequestParam(required = false) final String toDate) {
        SearchFilters filters = new SearchFilters();
        filters.setTerm(term);
        filters.setGameVersion(gameVersion);
        filters.setCountryCode(countryCode);
        filters.setPlatform(platform);
        filters.setFromDate(fromDate);
        filters.setToDate(toDate);
        return iapService.purchaseRatioByPlacement(filters);
    }

}
