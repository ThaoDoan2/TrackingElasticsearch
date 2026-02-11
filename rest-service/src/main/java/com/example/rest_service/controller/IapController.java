package com.example.rest_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rest_service.dto.IapDTO;
import com.example.rest_service.search.SearchFilters;
import com.example.rest_service.service.IIapService;

@RestController
@RequestMapping("/api/iap")
public class IapController {

    private final IIapService iapService;

    public IapController(IIapService iapService) {
        this.iapService = iapService;
    }

    @PostMapping
    public void save(@RequestBody final IapDTO iap) {
        iapService.save(iap);
    }

    @PostMapping("/search")
    public List<IapDTO> search(@RequestBody final SearchFilters filters) {
        return iapService.search(filters);
    }
}