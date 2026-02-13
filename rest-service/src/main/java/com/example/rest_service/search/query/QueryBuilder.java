package com.example.rest_service.search.query;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.rest_service.search.SearchFilters;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;

public final class QueryBuilder {

    private QueryBuilder() {

    }

    public static SearchRequest buildSearchRequest(final SearchFilters filters, final SearchMeta meta) {
        SearchRequest.Builder builder = new SearchRequest.Builder();
        builder.index(meta.getIndex());

        Query.Builder queryBuilder = new Query.Builder();
        if (meta.getType() == QueryType.MATCH) {
            final List<Query> shouldQueries = new ArrayList<>();
            final List<Query> filterQueries = new ArrayList<>();

            if (hasText(filters.getTerm())) {
                for (String field : meta.getFields()) {
                    shouldQueries.add(Query.of(q -> q.match(m -> m.field(field).query(filters.getTerm()))));
                }
            }

            addMultiValueExactFilter(filterQueries, "gameVersion.keyword", filters.getGameVersion());
            addMultiValueExactFilter(filterQueries, "country.keyword", filters.getCountryCode());
            addMultiValueExactFilter(filterQueries, "platform.keyword", filters.getPlatform());

            final String fromDate = normalizeDate(filters.getFromDate(), false);
            final String toDate = normalizeDate(filters.getToDate(), true);
            if (fromDate != null || toDate != null) {
                filterQueries.add(Query.of(q -> q.range(r -> r.date(d -> {
                    d.field("date");
                    if (fromDate != null) {
                        d.gte(fromDate);
                    }
                    if (toDate != null) {
                        d.lte(toDate);
                    }
                    return d;
                }))));
            }

            if (shouldQueries.isEmpty() && filterQueries.isEmpty()) {
                queryBuilder.matchAll(m -> m);
            } else {
                queryBuilder.bool(b -> {
                    if (!shouldQueries.isEmpty()) {
                        b.should(shouldQueries).minimumShouldMatch("1");
                    }
                    if (!filterQueries.isEmpty()) {
                        b.filter(filterQueries);
                    }
                    return b;
                });
            }
        }

        builder.query(queryBuilder.build());
        return builder.build();
    }

    public static SearchRequest buildIapSearchRequest(final SearchFilters filters, final SearchMeta meta){
SearchRequest.Builder builder = new SearchRequest.Builder();
        builder.index(meta.getIndex());

        Query.Builder queryBuilder = new Query.Builder();
        if (meta.getType() == QueryType.MATCH) {
            final List<Query> shouldQueries = new ArrayList<>();
            final List<Query> filterQueries = new ArrayList<>();

            if (hasText(filters.getTerm())) {
                for (String field : meta.getFields()) {
                    shouldQueries.add(Query.of(q -> q.match(m -> m.field(field).query(filters.getTerm()))));
                }
            }

            addMultiValueExactFilter(filterQueries, "gameVersion.keyword", filters.getGameVersion());
            addMultiValueExactFilter(filterQueries, "country.keyword", filters.getCountryCode());
            addMultiValueExactFilter(filterQueries, "platform.keyword", filters.getPlatform());

            final String fromDate = normalizeDate(filters.getFromDate(), false);
            final String toDate = normalizeDate(filters.getToDate(), true);
            if (fromDate != null || toDate != null) {
                filterQueries.add(Query.of(q -> q.range(r -> r.date(d -> {
                    d.field("date");
                    if (fromDate != null) {
                        d.gte(fromDate);
                    }
                    if (toDate != null) {
                        d.lte(toDate);
                    }
                    return d;
                }))));
            }

            if (shouldQueries.isEmpty() && filterQueries.isEmpty()) {
                queryBuilder.matchAll(m -> m);
            } else {
                queryBuilder.bool(b -> {
                    if (!shouldQueries.isEmpty()) {
                        b.should(shouldQueries).minimumShouldMatch("1");
                    }
                    if (!filterQueries.isEmpty()) {
                        b.filter(filterQueries);
                    }
                    return b;
                });
            }
        }

        builder.query(queryBuilder.build());
        return builder.build();
    }
    
    private static boolean hasText(final String value) {
        return value != null && !value.isBlank();
    }

    private static void addMultiValueExactFilter(final List<Query> filterQueries, final String fieldName,
            final List<String> values) {
        final List<Query> valueQueries = normalizeValues(values).stream()
                .map(value -> Query.of(q -> q.term(t -> t.field(fieldName).value(value))))
                .collect(Collectors.toList());
        if (!valueQueries.isEmpty()) {
            filterQueries.add(Query.of(q -> q.bool(b -> b.should(valueQueries).minimumShouldMatch("1"))));
        }
    }

    private static List<String> normalizeValues(final List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }

        return values.stream()
                .filter(QueryBuilder::hasText)
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());
    }

    private static String normalizeDate(final String rawDate, final boolean endOfDay) {
        if (!hasText(rawDate)) {
            return null;
        }

        final String trimmed = rawDate.trim();

        try {
            Instant.parse(trimmed);
            return trimmed;
        } catch (DateTimeParseException ignored) {
        }

        for (DateTimeFormatter formatter : List.of(DateTimeFormatter.ISO_LOCAL_DATE,
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
