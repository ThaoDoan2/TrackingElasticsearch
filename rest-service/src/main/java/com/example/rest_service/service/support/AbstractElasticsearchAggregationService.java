package com.example.rest_service.service.support;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.example.rest_service.feature.resource.service.ResourceServiceImpl;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;

public abstract class AbstractElasticsearchAggregationService {

    protected final ElasticsearchClient elasticsearchClient;

    protected AbstractElasticsearchAggregationService(final ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    protected List<String> getDistinctFieldValuesWithKeywordFallback(
            final Logger log,
            final String indexName,
            final String fieldName) {
        return getDistinctFieldValuesWithKeywordFallback(log, indexName, fieldName, List.of());
    }

    protected List<String> getDistinctFieldValuesWithKeywordFallback(
            final Logger log,
            final String indexName,
            final String fieldName,
            final List<String> gameIds) {
        try {
            return executeDistinctTermsQuery(indexName, fieldName, gameIds);
        } catch (Exception baseFieldError) {
            log.warn("Distinct query for field {} failed, fallback to {}.keyword. Root cause: {}", fieldName,
                    fieldName + ".keyword", baseFieldError.getMessage());
            try {
                return executeDistinctTermsQuery(indexName, fieldName + ".keyword", gameIds);
            } catch (Exception keywordFieldError) {
                log.error("Distinct query for field {} failed. Root cause: {}", fieldName,
                        keywordFieldError.getMessage(), keywordFieldError);
                return List.of();
            }
        }
    }

    protected List<String> executeDistinctTermsQuery(final String indexName, final String fieldName)
            throws IOException {
        return executeDistinctTermsQuery(indexName, fieldName, List.of());
    }

    protected List<String> executeDistinctTermsQuery(
            final String indexName,
            final String fieldName,
            final List<String> gameIds)
            throws IOException {
        SearchResponse<Void> response = elasticsearchClient.search(s -> s
                .index(indexName)
                .size(0)
                .query(buildGameScopeQuery(gameIds))
                .aggregations("values", a -> a.terms(t -> t.field(fieldName).size(1000))),
                Void.class);

        var values = response.aggregations().get("values");
        if (values == null || !values.isSterms()) {
            return List.of();
        }

        return new ArrayList<>(values.sterms().buckets().array().stream()
                .map(bucket -> bucket.key().stringValue())
                .filter(AbstractElasticsearchAggregationService::hasText)
                .map(String::trim)
                .collect(Collectors.toCollection(TreeSet::new)));
    }

    private static Query buildGameScopeQuery(final List<String> gameIds) {
        if (gameIds == null || gameIds.isEmpty()) {
            return Query.of(q -> q.matchAll(m -> m));
        }
        return Query.of(q -> q.bool(b -> b
                .should(gameIds.stream()
                        .map(gameId -> Query.of(inner -> inner.term(t -> t.field("gameId").value(gameId))))
                        .toList())
                .minimumShouldMatch("1")));
    }

    protected static boolean hasText(final String value) {
        return value != null && !value.isBlank();
    }

    protected static Long parseLong(final String value) {
        if (!hasText(value)) {
            return null;
        }

        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    protected static List<String> normalizeValues(final List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }

        return values.stream()
                .filter(AbstractElasticsearchAggregationService::hasText)
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());
    }

    protected static String normalizeDate(final String rawDate, final boolean endOfDay) {
        if (!hasText(rawDate)) {
            return null;
        }

        final String trimmed = rawDate.trim();
        try {
            Instant.parse(trimmed);
            return trimmed;
        } catch (DateTimeParseException ignored) {
        }

        for (DateTimeFormatter formatter : List.of(
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("MM/dd/yyyy"))) {
            try {
                LocalDate parsed = LocalDate.parse(trimmed, formatter);
                if (endOfDay) {
                    return parsed.atTime(23, 59, 59).toInstant(ZoneOffset.UTC).toString();
                }
                return parsed.atStartOfDay().toInstant(ZoneOffset.UTC).toString();
            } catch (DateTimeParseException ignored) {
            }
        }

        return trimmed;
    }
}
