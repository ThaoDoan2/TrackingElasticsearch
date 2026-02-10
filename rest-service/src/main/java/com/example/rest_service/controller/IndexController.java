package com.example.rest_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rest_service.service.index.IndexService;

@RestController
@RequestMapping("/api/index")
public class IndexController {

    private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);
    private final IndexService indexService;

    public IndexController(IndexService indexService) {
        this.indexService = indexService;
    }

    @PostMapping
    public void create(){
        LOG.info("POST /api/index called");
        indexService.createIndices();
    }
}
