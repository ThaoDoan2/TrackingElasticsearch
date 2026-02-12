package com.example.rest_service.service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.rest_service.dto.RewardedAmountByDayPlacementDTO;
import com.example.rest_service.dto.RewardedAmountByLevelDTO;
import com.example.rest_service.dto.RewardedAmountByLevelPlacementDTO;
import com.example.rest_service.dto.RewardedAdsFilterOptionsDTO;
import com.example.rest_service.search.SearchFilters;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;

@Service
public class RewardedAdsServiceImpl implements IRewardedAdsService {
    private static final Logger LOG = LoggerFactory.getLogger(RewardedAdsServiceImpl.class);
    private static final String INDEX = "rewarded_ads";

    private final ElasticsearchClient elasticsearchClient;

    public RewardedAdsServiceImpl(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    @Override
    public List<RewardedAmountByDayPlacementDTO> rewardedAmountPerDayGroupedByPlacement(SearchFilters filters) {
        try {
            final Query query = buildRewardedAdsQuery(filters);
            SearchResponse<Void> response = elasticsearchClient.search(s -> s
                    .index(INDEX)
                    .size(0)
                    .query(query)
                    .aggregations("by_date", a -> a
                            .dateHistogram(d -> d
                                    .field("date")
                                    .calendarInterval(CalendarInterval.Day)
                                    .format("yyyy-MM-dd")
                                    .minDocCount(1))
                            .aggregations("by_placement", sub -> sub
                                    .terms(t -> t.field("placement").size(1000).missing("UNKNOWN")))),
                    Void.class);

            List<RewardedAmountByDayPlacementDTO> rows = new ArrayList<>();
            var byDate = response.aggregations().get("by_date");
            if (byDate == null || !byDate.isDateHistogram()) {
                return rows;
            }

            for (var dateBucket : byDate.dateHistogram().buckets().array()) {
                Map<String, Long> placements = new LinkedHashMap<>();
                var byPlacement = dateBucket.aggregations().get("by_placement");
                if (byPlacement != null && byPlacement.isSterms()) {
                    for (var placementBucket : byPlacement.sterms().buckets().array()) {
                        placements.put(placementBucket.key().stringValue(), placementBucket.docCount());
                    }
                }
                rows.add(new RewardedAmountByDayPlacementDTO(dateBucket.keyAsString(), placements));
            }

            return rows;
        } catch (Exception error) {
            LOG.error("Rewarded amount per day grouped by placement query failed. Root cause: {}", error.getMessage(),
                    error);
            return List.of();
        }
    }

    @Override
    public RewardedAdsFilterOptionsDTO getFilterOptions() {
        return new RewardedAdsFilterOptionsDTO(
                getDistinctFieldValues("placement"),
                getDistinctFieldValues("gameVersion"),
                getDistinctFieldValues("country"),
                getDistinctFieldValues("platform"));
    }

    @Override
    public List<String> getAllCountries() {
        return getDistinctFieldValues("country");
    }

    @Override
    public List<String> getAllPlacements() {
        return getDistinctFieldValues("placement");
    }

    @Override
    public List<String> getAllGameVersions() {
        return getDistinctFieldValues("gameVersion");
    }

    @Override
    public List<String> getAllPlatforms() {
        return getDistinctFieldValues("platform");
    }

    @Override
    public List<RewardedAmountByLevelDTO> rewardedAmountPerLevel(SearchFilters filters) {
        try {
            final Query query = buildRewardedAdsQuery(filters);
            SearchResponse<Void> response = executeAmountPerLevelQuery(query, "level", false);
            return mapAmountPerLevelResponse(response);
        } catch (Exception levelError) {
            LOG.warn("Rewarded amount per level query with level failed, fallback to level.keyword. Root cause: {}",
                    levelError.getMessage());
            try {
                final Query query = buildRewardedAdsQuery(filters);
                SearchResponse<Void> response = executeAmountPerLevelQuery(query, "level.keyword", true);
                return mapAmountPerLevelResponse(response);
            } catch (Exception fallbackError) {
                LOG.error("Rewarded amount per level query failed. Root cause: {}", fallbackError.getMessage(),
                        fallbackError);
                return List.of();
            }
        }
    }

    @Override
    public List<RewardedAmountByLevelPlacementDTO> rewardedAmountPerLevelGroupedByPlacement(SearchFilters filters) {
        try {
            final Query query = buildRewardedAdsQuery(filters);
            SearchResponse<Void> response = executeAmountPerLevelPlacementQuery(query, "level", false);
            return mapAmountPerLevelPlacementResponse(response);
        } catch (Exception levelError) {
            LOG.warn(
                    "Rewarded amount per level grouped by placement query with level failed, fallback to level.keyword. Root cause: {}",
                    levelError.getMessage());
            try {
                final Query query = buildRewardedAdsQuery(filters);
                SearchResponse<Void> response = executeAmountPerLevelPlacementQuery(query, "level.keyword", true);
                return mapAmountPerLevelPlacementResponse(response);
            } catch (Exception fallbackError) {
                LOG.error("Rewarded amount per level grouped by placement query failed. Root cause: {}",
                        fallbackError.getMessage(),
                        fallbackError);
                return List.of();
            }
        }
    }

    private SearchResponse<Void> executeAmountPerLevelQuery(final Query query, final String levelField,
            final boolean includeMissingUnknown)
            throws IOException {
        return elasticsearchClient.search(s -> s
                .index(INDEX)
                .size(0)
                .query(query)
                .aggregations("by_level", a -> a
                        .terms(t -> {
                            t.field(levelField).size(1000);
                            if (includeMissingUnknown) {
                                t.missing("UNKNOWN");
                            }
                            return t;
                        })),
                Void.class);
    }

    private SearchResponse<Void> executeAmountPerLevelPlacementQuery(final Query query, final String levelField,
            final boolean includeMissingUnknown)
            throws IOException {
        return elasticsearchClient.search(s -> s
                .index(INDEX)
                .size(0)
                .query(query)
                .aggregations("by_level", a -> a
                        .terms(t -> {
                            t.field(levelField).size(1000);
                            if (includeMissingUnknown) {
                                t.missing("UNKNOWN");
                            }
                            return t;
                        })
                        .aggregations("by_placement", sub -> sub.terms(t -> t.field("placement").size(50)))),
                Void.class);
    }

    private List<RewardedAmountByLevelDTO> mapAmountPerLevelResponse(SearchResponse<Void> response) {
        List<RewardedAmountByLevelDTO> rows = new ArrayList<>();
        var byLevel = response.aggregations().get("by_level");
        if (byLevel == null) {
            return rows;
        }

        if (byLevel.isSterms()) {
            for (var levelBucket : byLevel.sterms().buckets().array()) {
                rows.add(new RewardedAmountByLevelDTO(levelBucket.key().stringValue(), levelBucket.docCount()));
            }
            return rows;
        }

        if (byLevel.isLterms()) {
            for (var levelBucket : byLevel.lterms().buckets().array()) {
                rows.add(new RewardedAmountByLevelDTO(String.valueOf(levelBucket.key()), levelBucket.docCount()));
            }
        }

        return rows;
    }

    private List<RewardedAmountByLevelPlacementDTO> mapAmountPerLevelPlacementResponse(SearchResponse<Void> response) {
        List<RewardedAmountByLevelPlacementDTO> rows = new ArrayList<>();
        var byLevel = response.aggregations().get("by_level");
        if (byLevel == null) {
            return rows;
        }

        if (byLevel.isSterms()) {
            for (var levelBucket : byLevel.sterms().buckets().array()) {
                rows.add(new RewardedAmountByLevelPlacementDTO(
                        levelBucket.key().stringValue(),
                        extractPlacements(levelBucket.aggregations().get("by_placement"))));
            }
            return rows;
        }

        if (byLevel.isLterms()) {
            for (var levelBucket : byLevel.lterms().buckets().array()) {
                rows.add(new RewardedAmountByLevelPlacementDTO(
                        String.valueOf(levelBucket.key()),
                        extractPlacements(levelBucket.aggregations().get("by_placement"))));
            }
        }

        return rows;
    }

    private Map<String, Long> extractPlacements(final Aggregate byPlacement) {
        Map<String, Long> placements = new LinkedHashMap<>();
        if (byPlacement == null) {
            return placements;
        }

        if (byPlacement.isSterms()) {
            for (var placementBucket : byPlacement.sterms().buckets().array()) {
                placements.put(placementBucket.key().stringValue(), placementBucket.docCount());
            }
            return placements;
        }

        if (byPlacement.isLterms()) {
            for (var placementBucket : byPlacement.lterms().buckets().array()) {
                placements.put(String.valueOf(placementBucket.key()), placementBucket.docCount());
            }
        }

        return placements;
    }

    private Query buildRewardedAdsQuery(SearchFilters filters) {
        final List<Query> filterQueries = new ArrayList<>();
        final List<Query> shouldQueries = new ArrayList<>();

        if (hasText(filters.getGameVersion())) {
            filterQueries.add(
                    Query.of(q -> q.term(t -> t.field("gameVersion").value(filters.getGameVersion()))));
        }
        if (hasText(filters.getCountryCode())) {
            filterQueries.add(Query.of(q -> q.term(t -> t.field("country").value(filters.getCountryCode()))));
        }
        if (hasText(filters.getPlatform())) {
            filterQueries.add(Query.of(q -> q.term(t -> t.field("platform").value(filters.getPlatform()))));
        }
        if (filters.getPlacements() != null && !filters.getPlacements().isEmpty()) {
            final List<Query> placementQueries = filters.getPlacements().stream()
                    .filter(RewardedAdsServiceImpl::hasText)
                    .map(String::trim)
                    .distinct()
                    .map(placement -> Query.of(q -> q.term(t -> t.field("placement").value(placement))))
                    .collect(Collectors.toList());
            if (!placementQueries.isEmpty()) {
                filterQueries.add(Query.of(q -> q.bool(b -> b.should(placementQueries).minimumShouldMatch("1"))));
            }
        }

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

        if (hasText(filters.getTerm())) {
            shouldQueries.add(Query.of(q -> q.match(m -> m.field("userId").query(filters.getTerm()))));
            shouldQueries.add(Query.of(q -> q.match(m -> m.field("placement").query(filters.getTerm()))));
            shouldQueries.add(Query.of(q -> q.match(m -> m.field("subPlacement").query(filters.getTerm()))));
        }

        if (filterQueries.isEmpty() && shouldQueries.isEmpty()) {
            return Query.of(q -> q.matchAll(m -> m));
        }

        return Query.of(q -> q.bool(b -> {
            if (!filterQueries.isEmpty()) {
                b.filter(filterQueries);
            }
            if (!shouldQueries.isEmpty()) {
                b.should(shouldQueries).minimumShouldMatch("1");
            }
            return b;
        }));
    }

    private List<String> getDistinctFieldValues(final String fieldName) {
        try {
            return executeDistinctTermsQuery(fieldName);
        } catch (Exception baseFieldError) {
            LOG.warn("Distinct query for field {} failed, fallback to {}.keyword. Root cause: {}", fieldName,
                    fieldName + ".keyword", baseFieldError.getMessage());
            try {
                return executeDistinctTermsQuery(fieldName + ".keyword");
            } catch (Exception keywordFieldError) {
                LOG.error("Distinct query for field {} failed. Root cause: {}", fieldName,
                        keywordFieldError.getMessage(), keywordFieldError);
                return List.of();
            }
        }
    }

    private List<String> executeDistinctTermsQuery(final String fieldName) throws IOException {
        SearchResponse<Void> response = elasticsearchClient.search(s -> s
                .index(INDEX)
                .size(0)
                .aggregations("values", a -> a.terms(t -> t.field(fieldName).size(1000))),
                Void.class);

        var values = response.aggregations().get("values");
        if (values == null || !values.isSterms()) {
            return List.of();
        }

        // TreeSet keeps values unique and sorted for stable filter dropdowns.
        return new ArrayList<>(values.sterms().buckets().array().stream()
                .map(bucket -> bucket.key().stringValue())
                .filter(RewardedAdsServiceImpl::hasText)
                .map(String::trim)
                .collect(Collectors.toCollection(TreeSet::new)));
    }

    private static boolean hasText(final String value) {
        return value != null && !value.isBlank();
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
