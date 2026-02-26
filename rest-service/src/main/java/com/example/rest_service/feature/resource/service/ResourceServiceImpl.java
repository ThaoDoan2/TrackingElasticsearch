package com.example.rest_service.feature.resource.service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.rest_service.feature.resource.dto.ResourceDTO;
import com.example.rest_service.feature.resource.dto.ResourceSourceSinkByDateDTO;
import com.example.rest_service.feature.resource.dto.ResourceSourceSinkByLevelDTO;
import com.example.rest_service.feature.resource.dto.ResourceWhereMainDTO;
import com.example.rest_service.feature.resource.repository.ResourceDocument;
import com.example.rest_service.feature.resource.repository.ResourceRepository;
import com.example.rest_service.feature.user.service.UserAccountService;
import com.example.rest_service.search.ElasticsearchProxy;
import com.example.rest_service.search.SearchFilters;
import com.example.rest_service.search.query.QueryType;
import com.example.rest_service.search.query.SearchMeta;
import com.example.rest_service.feature.resource.service.converter.ResourceDTOConverter;
import com.example.rest_service.service.support.AbstractElasticsearchAggregationService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.util.NamedValue;

@Service
public class ResourceServiceImpl extends AbstractElasticsearchAggregationService implements IResourceService {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceServiceImpl.class);
    private static final String INDEX = "resource";
    private static final List<String> SOURCE_SINK_TYPES = List.of("Source", "Sink", "source", "sink");
    private static final List<String> SOURCE_TYPES = List.of("Source", "source");
    private static final List<String> SINK_TYPES = List.of("Sink", "sink");


    private final ResourceRepository repository;
    private final ResourceDTOConverter converter;
    private final ElasticsearchProxy<ResourceDocument, ResourceDTO> client;
    private final UserAccountService userAccountService;
    public ResourceServiceImpl(ResourceRepository repository,
            ResourceDTOConverter converter,
            ElasticsearchProxy<ResourceDocument, ResourceDTO> client,
            ElasticsearchClient elasticsearchClient,
            UserAccountService userAccountService) {
        super(elasticsearchClient);
        this.repository = repository;
        this.converter = converter;
        this.client = client;
        this.userAccountService = userAccountService;
    }

    @Override
    public void save(ResourceDocument resource) {
        repository.save(resource);
    }

    @Override
    public void save(ResourceDTO resource) {
        repository.save(converter.convertToDocument(resource));
    }

    @Override
    public List<ResourceDTO> search(SearchFilters filters) {
        userAccountService.applyGameScope(filters);
        return client.search(
                filters,
                new SearchMeta(List.of("userId", "gameId", "eventType", "itemName"), "resource", QueryType.MATCH),
                ResourceDocument.class);
    }

    @Override
    public List<ResourceSourceSinkByDateDTO> sourceSinkByDate(SearchFilters filters) {
        userAccountService.applyGameScope(filters);
        try {
            final Query query = buildResourceChartQuery(filters, SOURCE_SINK_TYPES);
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
                            .aggregations("by_type", sub -> sub
                                    .terms(t -> t.field("eventType").size(10))
                                    .aggregations("total_amount", amount -> amount.sum(sum -> sum.field("amount"))))),
                    Void.class);

            List<ResourceSourceSinkByDateDTO> rows = new ArrayList<>();
            var byDate = response.aggregations().get("by_date");
            if (byDate == null || !byDate.isDateHistogram()) {
                return rows;
            }

            for (var dateBucket : byDate.dateHistogram().buckets().array()) {
                long source = 0L;
                long sink = 0L;

                var byType = dateBucket.aggregations().get("by_type");
                if (byType != null && byType.isSterms()) {
                    for (var typeBucket : byType.sterms().buckets().array()) {
                        final String eventType = typeBucket.key().stringValue();
                        final long amount = extractSumAsLong(typeBucket.aggregations().get("total_amount"));
                        if ("source".equalsIgnoreCase(eventType)) {
                            source += amount;
                        } else if ("sink".equalsIgnoreCase(eventType)) {
                            sink += amount;
                        }
                    }
                }
                rows.add(new ResourceSourceSinkByDateDTO(dateBucket.keyAsString(), source, sink));
            }
            return rows;
        } catch (Exception error) {
            LOG.error("Resource source/sink by date query failed. Root cause: {}", error.getMessage(), error);
            return List.of();
        }
    }

    @Override
    public List<ResourceSourceSinkByLevelDTO> sourceSinkByLevel(SearchFilters filters) {
        userAccountService.applyGameScope(filters);
        try {
            final Query query = buildResourceChartQuery(filters, SOURCE_SINK_TYPES);
            SearchResponse<Void> response = elasticsearchClient.search(s -> s
                    .index(INDEX)
                    .size(0)
                    .query(query)
                    .aggregations("by_level", a -> a
                            .terms(t -> t
                                    .field("highestLevel")
                                    .size(2000)
                                    .order(NamedValue.of("_key", SortOrder.Asc)))
                            .aggregations("by_type", sub -> sub
                                    .terms(t -> t.field("eventType").size(10))
                                    .aggregations("total_amount", amount -> amount.sum(sum -> sum.field("amount"))))),
                    Void.class);

            List<ResourceSourceSinkByLevelDTO> rows = new ArrayList<>();
            var byLevel = response.aggregations().get("by_level");
            if (byLevel == null) {
                return rows;
            }

            if (byLevel.isLterms()) {
                for (var levelBucket : byLevel.lterms().buckets().array()) {
                    long source = 0L;
                    long sink = 0L;
                    var byType = levelBucket.aggregations().get("by_type");
                    if (byType != null && byType.isSterms()) {
                        for (var typeBucket : byType.sterms().buckets().array()) {
                            final String eventType = typeBucket.key().stringValue();
                            final long amount = extractSumAsLong(typeBucket.aggregations().get("total_amount"));
                            if ("source".equalsIgnoreCase(eventType)) {
                                source += amount;
                            } else if ("sink".equalsIgnoreCase(eventType)) {
                                sink += amount;
                            }
                        }
                    }
                    rows.add(new ResourceSourceSinkByLevelDTO(levelBucket.key(), source, sink));
                }
            } else if (byLevel.isSterms()) {
                for (var levelBucket : byLevel.sterms().buckets().array()) {
                    final Long level = parseLong(levelBucket.key().stringValue());
                    if (level == null) {
                        continue;
                    }
                    long source = 0L;
                    long sink = 0L;
                    var byType = levelBucket.aggregations().get("by_type");
                    if (byType != null && byType.isSterms()) {
                        for (var typeBucket : byType.sterms().buckets().array()) {
                            final String eventType = typeBucket.key().stringValue();
                            final long amount = extractSumAsLong(typeBucket.aggregations().get("total_amount"));
                            if ("source".equalsIgnoreCase(eventType)) {
                                source += amount;
                            } else if ("sink".equalsIgnoreCase(eventType)) {
                                sink += amount;
                            }
                        }
                    }
                    rows.add(new ResourceSourceSinkByLevelDTO(level, source, sink));
                }
            }
            return rows;
        } catch (Exception error) {
            LOG.error("Resource source/sink by level query failed. Root cause: {}", error.getMessage(), error);
            return List.of();
        }
    }

    @Override
    public List<ResourceWhereMainDTO> sourceByWhereMain(SearchFilters filters) {
        userAccountService.applyGameScope(filters);
        return amountByWhereMain(filters, SOURCE_TYPES);
    }

    @Override
    public List<ResourceWhereMainDTO> sinkByWhereMain(SearchFilters filters) {
        userAccountService.applyGameScope(filters);
        return amountByWhereMain(filters, SINK_TYPES);
    }

    @Override
    public List<String> getAllPlatforms() {
        return getDistinctFieldValues("platform");
    }

    @Override
    public List<String> getAllPlacements() {
        return getDistinctFieldValues("placement");
    }

    @Override
    public List<String> getAllSubPlacements() {
        return getDistinctFieldValues("subPlacement");
    }

    @Override
    public List<String> getAllCountries() {
        return getDistinctFieldValues("country");
    }

    @Override
    public List<String> getAllGameVersions() {
        return getDistinctFieldValues("gameVersion");
    }

    @Override
    public List<String> getAllItemNames() {
        return getDistinctFieldValues("itemName");
    }

    @Override
    public void initData() {
        String countries[] = { "US", "UK", "DE", "FR", "JP" };
        String versions[] = { "1.0.0", "1.1.0", "1.2.0" };

        int itemIds[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
        String itemNams[] = { "Gold", "Hammer", "Bonus", "Clock", "Hint", "TimeStop", "Shuffle", "Transform", "Life" };
        String placements[] = { "BattlePass", "Rewarded", "DailyQuest", "Prepare", "CardCollection", "Shop" };
        String subPlacements[] = { "Training", "Starter", "BasicBundle", "BigBundle" };
        for (int i = 0; i < 100; i++) {
            int r = ThreadLocalRandom.current().nextInt(1000);
            int r2 = ThreadLocalRandom.current().nextInt(1000);
            ResourceDocument doc = new ResourceDocument();
            doc.setUserId("user" + (r % 50));
            doc.setGameId("com.higame.goods.sorting.match.triple.master");
            doc.setEventType(r % 3 == 0 ? "Sink" : "Source");
            doc.setPlatform(r % 2 == 0 ? "Android" : "iOS");
            doc.setCountry(countries[r % countries.length]);
            doc.setGameVersion(versions[r % versions.length]);
            doc.setLoggedDay((long) r % 7);
            doc.setHighestLevel((long) (r % 2500));
            Date now = new Date();
            Date accountCreatedDate = new Date(now.getTime() - r * 86400 * 1000L);
            doc.setAccountCreatedDate(accountCreatedDate);
            Date recordDate = new Date(now.getTime() - (r % 7) * 86400 * 1000L);
            doc.setDate(recordDate);

            doc.setItemId(itemIds[r % itemIds.length]);
            doc.setItemName(itemNams[r % itemNams.length]);
            doc.setAmount((long) r * 10);

            if (doc.getEventType().equals("Source")) {
                doc.setPlacement(placements[r % placements.length]);
                if (doc.getPlacement().equals("Shop")) {
                    doc.setSubPlacement(subPlacements[r2 % subPlacements.length]);
                }
            } else {
                doc.setPlacement(r2 % 2 == 0 ? "Prepare" : "InGame");
            }
            repository.save(doc);
        }
        LOG.info("Sample data initialized");
    }

    private List<ResourceWhereMainDTO> amountByWhereMain(SearchFilters filters, List<String> eventTypes) {
        try {
            final Query query = buildResourceChartQuery(filters, eventTypes);
            SearchResponse<Void> response = elasticsearchClient.search(s -> s
                    .index(INDEX)
                    .size(0)
                    .query(query)
                    .aggregations("by_placement", a -> a
                            .terms(t -> t.field("placement").size(500).missing(""))
                            .aggregations("by_sub_placement", sub -> sub
                                    .terms(t -> t.field("subPlacement").size(500).missing(""))
                                    .aggregations("total_amount", amount -> amount.sum(sum -> sum.field("amount"))))),
                    Void.class);

            final Aggregate byPlacement = response.aggregations().get("by_placement");
            if (byPlacement == null || !byPlacement.isSterms()) {
                return List.of();
            }

            List<ResourceWhereMainDTO> rows = new ArrayList<>();
            long total = 0L;
            for (var placementBucket : byPlacement.sterms().buckets().array()) {
                final String placement = safeText(placementBucket.key().stringValue());
                final Aggregate bySubPlacement = placementBucket.aggregations().get("by_sub_placement");
                if (bySubPlacement == null || !bySubPlacement.isSterms()) {
                    continue;
                }

                for (var subBucket : bySubPlacement.sterms().buckets().array()) {
                    final String subPlacement = safeText(subBucket.key().stringValue());
                    final long amount = extractSumAsLong(subBucket.aggregations().get("total_amount"));
                    if (amount <= 0L) {
                        continue;
                    }
                    total += amount;
                    rows.add(new ResourceWhereMainDTO(buildWhereMainLabel(placement, subPlacement), amount, 0D));
                }
            }

            if (total <= 0L || rows.isEmpty()) {
                return List.of();
            }
            final long grandTotal = total;

            return rows.stream()
                    .map(row -> new ResourceWhereMainDTO(
                            row.getWhereMain(),
                            row.getAmount(),
                            (row.getAmount() * 100D) / grandTotal))
                    .sorted((a, b) -> Long.compare(b.getAmount(), a.getAmount()))
                    .toList();
        } catch (Exception error) {
            LOG.error("Resource whereMain chart query failed. Root cause: {}", error.getMessage(), error);
            return List.of();
        }
    }

    private Query buildResourceChartQuery(final SearchFilters filters, final List<String> eventTypes) {
        final List<Query> filterQueries = new ArrayList<>();
        final List<Query> shouldQueries = new ArrayList<>();

        addMultiValueExactFilter(filterQueries, "gameVersion", filters.getGameVersion());
        addMultiValueExactFilter(filterQueries, "gameId", filters.getGameIds());
        addMultiValueExactFilter(filterQueries, "country", filters.getCountryCode());
        addMultiValueExactFilter(filterQueries, "platform", filters.getPlatform());
        addMultiValueExactFilter(filterQueries, "placement", filters.getPlacements());
        addMultiValueExactFilter(filterQueries, "subPlacement", filters.getSubPlacements());
        addMultiValueExactFilter(filterQueries, "itemName", filters.getItemNames());
        addMultiValueExactFilter(filterQueries, "eventType", eventTypes);

        if (filters.getMinLevel() != null || filters.getMaxLevel() != null) {
            filterQueries.add(Query.of(q -> q.range(r -> r.number(n -> {
                n.field("highestLevel");
                if (filters.getMinLevel() != null) {
                    n.gte(filters.getMinLevel().doubleValue());
                }
                if (filters.getMaxLevel() != null) {
                    n.lte(filters.getMaxLevel().doubleValue());
                }
                return n;
            }))));
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
            shouldQueries.add(Query.of(q -> q.match(m -> m.field("gameId").query(filters.getTerm()))));
            shouldQueries.add(Query.of(q -> q.match(m -> m.field("itemName").query(filters.getTerm()))));
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

    private static void addMultiValueExactFilter(final List<Query> filterQueries, final String fieldName,
            final List<String> values) {
        final List<Query> valueQueries = normalizeValues(values).stream()
                .map(value -> Query.of(q -> q.term(t -> t.field(fieldName).value(value))))
                .collect(Collectors.toList());
        if (!valueQueries.isEmpty()) {
            filterQueries.add(Query.of(q -> q.bool(b -> b.should(valueQueries).minimumShouldMatch("1"))));
        }
    }


    private static Long extractSumAsLong(final Aggregate aggregate) {
        if (aggregate == null) {
            return 0L;
        }
        if (aggregate.isSum() && aggregate.sum().value() != null) {
            return Math.round(aggregate.sum().value());
        }
        if (aggregate.isSimpleValue() && aggregate.simpleValue().value() != null) {
            return Math.round(aggregate.simpleValue().value());
        }
        return 0L;
    }

    private static String safeText(final String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    private static String buildWhereMainLabel(final String placement, final String subPlacement) {
        return safeText(placement) + "(" + safeText(subPlacement) + ")";
    }

    private List<String> getDistinctFieldValues(final String fieldName) {
        return getDistinctFieldValuesWithKeywordFallback(
                LOG,
                INDEX,
                fieldName,
                userAccountService.getCurrentUserGameScopeOrEmptyForAdmin());
    }
}
