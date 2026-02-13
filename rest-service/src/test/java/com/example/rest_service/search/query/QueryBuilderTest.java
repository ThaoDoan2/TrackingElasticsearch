package com.example.rest_service.search.query;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.example.rest_service.search.SearchFilters;

import co.elastic.clients.elasticsearch.core.SearchRequest;

class QueryBuilderTest {

    
    void shouldBuildQueryWithTermGameVersionAndDateRange() {
        SearchFilters filters = new SearchFilters();
        filters.setTerm("user99");
        filters.setGameVersion(List.of("01"));
        filters.setFromDate("01/01/2026");
        filters.setToDate("02/11/2026");

        SearchRequest request = QueryBuilder.buildSearchRequest(
                filters,
                new SearchMeta(List.of("userId", "productId", "transactionId"), "iap", QueryType.MATCH));

        assertEquals("iap", request.index().get(0));
        assertNotNull(request.query());
        assertNotNull(request.query().bool());
        assertEquals("1", request.query().bool().minimumShouldMatch());
        assertEquals(3, request.query().bool().should().size());
        assertEquals(2, request.query().bool().filter().size());
        String queryJson = request.query().toString();
        assertTrue(queryJson.contains("\"gameVersion\""));
        assertTrue(queryJson.contains("\"01\""));
        assertTrue(queryJson.contains("\"date\""));
        assertTrue(queryJson.contains("\"2026-01-01T00:00:00Z\""));
        assertTrue(queryJson.contains("\"2026-02-11T23:59:59Z\""));
    }

    @Test
    void shouldBuildMatchAllWhenNoFiltersProvided() {
        SearchFilters filters = new SearchFilters();

        SearchRequest request = QueryBuilder.buildSearchRequest(
                filters,
                new SearchMeta(List.of("userId"), "iap", QueryType.MATCH));

        assertNotNull(request.query());
        assertNotNull(request.query().matchAll());
    }
}
