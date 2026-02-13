package com.example.rest_service.service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.rest_service.dto.IapChartCompactRowDTO;
import com.example.rest_service.dto.IapChartResponse;
import com.example.rest_service.dto.IapChartSeriesDTO;
import com.example.rest_service.dto.IapDailyProductTotalDTO;
import com.example.rest_service.dto.IapDTO;
import com.example.rest_service.dto.IapPlacementRatioDTO;
import com.example.rest_service.repository.iap.IapDocument;
import com.example.rest_service.repository.iap.IapRepository;
import com.example.rest_service.search.ElasticsearchProxy;
import com.example.rest_service.search.SearchFilters;
import com.example.rest_service.search.query.QueryType;
import com.example.rest_service.search.query.SearchMeta;
import com.example.rest_service.service.converter.IapDTOConverter;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;

@Service
public class IapServiceImpl implements IIapService {
    private static final Logger LOG = LoggerFactory.getLogger(IapServiceImpl.class);

    private final IapRepository repository;
    private final IapDTOConverter converter;
    private final ElasticsearchProxy<IapDocument, IapDTO> client;
    private final ElasticsearchClient elasticsearchClient;

    public IapServiceImpl(IapRepository repository,
            IapDTOConverter converter,
            ElasticsearchProxy<IapDocument, IapDTO> client,
            ElasticsearchClient elasticsearchClient) {
        this.repository = repository;
        this.converter = converter;
        this.client = client;
        this.elasticsearchClient = elasticsearchClient;
    }

    @Override
    public void save(IapDocument iap) {
        repository.save(iap);
    }

    @Override
    public void save(IapDTO iap) {
        repository.save(converter.convertToDocument(iap));
    }

    @Override
    public List<IapDTO> search(SearchFilters filters) {
        return client.search(
                filters,
                new SearchMeta(List.of("userId", "productId", "transactionId"), "iap", QueryType.MATCH),
                IapDocument.class);
    }

    @Override
    public IapChartResponse chart(SearchFilters filters) {
        try {
            return executeChartQuery(filters, "productId.keyword", "gameVersion.keyword");
        } catch (Exception error) {
            LOG.error("IAP chart query failed. Root cause: {}", error.getMessage(), error);
            return new IapChartResponse(List.of(), List.of());
        }
    }

    @Override
    public List<IapChartCompactRowDTO> chartCompact(SearchFilters filters) {
        try {
            return executeCompactChartQuery(filters, "productId.keyword", "gameVersion.keyword");
        } catch (Exception error) {
            LOG.error("IAP compact chart query failed. Root cause: {}", error.getMessage(), error);
            return List.of();
        }
    }

    @Override
    public List<IapDailyProductTotalDTO> totalPurchasePerDay(SearchFilters filters) {
        try {
            return executeTotalPurchasePerDayQuery(filters, "productId.keyword", "gameVersion.keyword");
        } catch (Exception error) {
            LOG.error("IAP total purchase query failed. Root cause: {}", error.getMessage(), error);
            return List.of();
        }
    }

    @Override
    public List<IapPlacementRatioDTO> purchaseRatioByPlacement(SearchFilters filters) {
        try {
            return executePurchaseRatioByPlacementQuery(filters, "placement.keyword", "gameVersion.keyword");
        } catch (Exception error) {
            LOG.error("IAP purchase ratio by placement query failed. Root cause: {}", error.getMessage(), error);
            return List.of();
        }
    }

    @Override
    public List<String> getAllPlatforms() {
        try {
            SearchResponse<Void> response = elasticsearchClient.search(s -> s
                    .index("iap")
                    .size(0)
                    .aggregations("all_platforms", a -> a
                            .terms(t -> t.field("platform.keyword").size(1000))),
                    Void.class);

            var allPlatforms = response.aggregations().get("all_platforms");
            if (allPlatforms == null || !allPlatforms.isSterms()) {
                return List.of();
            }

            return allPlatforms.sterms().buckets().array().stream()
                    .map(bucket -> bucket.key().stringValue())
                    .toList();
        } catch (Exception error) {
            LOG.error("IAP all platforms query failed. Root cause: {}", error.getMessage(), error);
            return List.of();
        }
    }

    private IapChartResponse executeChartQuery(SearchFilters filters, String productField, String gameVersionField)
            throws IOException {
        SearchResponse<Void> response = executeChartAggregationQuery(filters, productField, gameVersionField);

        Map<String, Map<String, Long>> chartMatrix = new TreeMap<>();
        Map<String, Long> totalsByProduct = new HashMap<>();

        var byDate = response.aggregations().get("by_date");
        if (byDate != null && byDate.isDateHistogram()) {
            for (var dateBucket : byDate.dateHistogram().buckets().array()) {
                final String label = dateBucket.keyAsString();
                Map<String, Long> productCountMap = new HashMap<>();

                var byProduct = dateBucket.aggregations().get("by_product");
                if (byProduct != null && byProduct.isSterms()) {
                    for (var productBucket : byProduct.sterms().buckets().array()) {
                        String productId = productBucket.key().stringValue();
                        long count = productBucket.docCount();
                        productCountMap.put(productId, count);
                        totalsByProduct.merge(productId, count, Long::sum);
                    }
                }

                chartMatrix.put(label, productCountMap);
            }
        }

        List<String> labels = new ArrayList<>(chartMatrix.keySet());
        List<String> orderedProducts = totalsByProduct.entrySet()
                .stream()
                .sorted((left, right) -> Long.compare(right.getValue(), left.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        List<IapChartSeriesDTO> series = new ArrayList<>();
        for (String productId : orderedProducts) {
            List<Long> values = labels.stream()
                    .map(dateLabel -> chartMatrix.get(dateLabel).getOrDefault(productId, 0L))
                    .toList();
            series.add(new IapChartSeriesDTO(productId, values));
        }

        return new IapChartResponse(labels, series);
    }

    private List<IapChartCompactRowDTO> executeCompactChartQuery(SearchFilters filters, String productField,
            String gameVersionField) throws IOException {
        SearchResponse<Void> response = executeChartAggregationQuery(filters, productField, gameVersionField);
        List<IapChartCompactRowDTO> rows = new ArrayList<>();

        var byDate = response.aggregations().get("by_date");
        if (byDate == null || !byDate.isDateHistogram()) {
            return rows;
        }

        for (var dateBucket : byDate.dateHistogram().buckets().array()) {
            Map<String, Long> products = new LinkedHashMap<>();
            var byProduct = dateBucket.aggregations().get("by_product");
            if (byProduct != null && byProduct.isSterms()) {
                for (var productBucket : byProduct.sterms().buckets().array()) {
                    products.put(productBucket.key().stringValue(), productBucket.docCount());
                }
            }
            rows.add(new IapChartCompactRowDTO(dateBucket.keyAsString(), products));
        }

        return rows;
    }

    private SearchResponse<Void> executeChartAggregationQuery(SearchFilters filters, String productField,
            String gameVersionField) throws IOException {
        final Query query = buildChartQuery(filters, gameVersionField);
        return elasticsearchClient.search(s -> s
                .index("iap")
                .size(0)
                .query(query)
                .aggregations("by_date", a -> a
                        .dateHistogram(d -> d
                                .field("date")
                                .calendarInterval(CalendarInterval.Day)
                                .format("yyyy-MM-dd")
                                .minDocCount(1))
                        .aggregations("by_product", sub -> sub
                                .terms(t -> t.field(productField).size(1000)))),
                Void.class);
    }

    private List<IapDailyProductTotalDTO> executeTotalPurchasePerDayQuery(SearchFilters filters, String productField,
            String gameVersionField) throws IOException {
        final Query query = buildChartQuery(filters, gameVersionField);
        SearchResponse<Void> response = elasticsearchClient.search(s -> s
                .index("iap")
                .size(0)
                .query(query)
                .aggregations("by_date", a -> a
                        .dateHistogram(d -> d
                                .field("date")
                                .calendarInterval(CalendarInterval.Day)
                                .format("yyyy-MM-dd")
                                .minDocCount(1))
                        .aggregations("by_product", sub -> sub
                                .terms(t -> t.field(productField).size(1000))
                                .aggregations("total_price", priceAgg -> priceAgg.sum(sum -> sum.field("price"))))),
                Void.class);

        List<IapDailyProductTotalDTO> rows = new ArrayList<>();
        var byDate = response.aggregations().get("by_date");
        if (byDate == null || !byDate.isDateHistogram()) {
            return rows;
        }

        for (var dateBucket : byDate.dateHistogram().buckets().array()) {
            Map<String, Double> products = new LinkedHashMap<>();
            var byProduct = dateBucket.aggregations().get("by_product");
            if (byProduct != null && byProduct.isSterms()) {
                for (var productBucket : byProduct.sterms().buckets().array()) {
                    double totalPrice = 0D;
                    var totalPriceAgg = productBucket.aggregations().get("total_price");
                    if (totalPriceAgg != null && totalPriceAgg.isSum()) {
                        Double value = totalPriceAgg.sum().value();
                        if (value != null) {
                            totalPrice = value;
                        }
                    }
                    products.put(productBucket.key().stringValue(), totalPrice);
                }
            }
            rows.add(new IapDailyProductTotalDTO(dateBucket.keyAsString(), products));
        }

        return rows;
    }

    private List<IapPlacementRatioDTO> executePurchaseRatioByPlacementQuery(SearchFilters filters, String placementField,
            String gameVersionField) throws IOException {
        final Query query = buildChartQuery(filters, gameVersionField);
        SearchResponse<Void> response = elasticsearchClient.search(s -> s
                .index("iap")
                .size(0)
                .query(query)
                .aggregations("by_placement", a -> a
                        .terms(t -> t.field(placementField).size(1000).missing("UNKNOWN"))
                        .aggregations("total_revenue", sub -> sub.sum(sum -> sum.field("price")))),
                Void.class);

        List<IapPlacementRatioDTO> rows = new ArrayList<>();
        var byPlacement = response.aggregations().get("by_placement");
        if (byPlacement == null || !byPlacement.isSterms()) {
            return rows;
        }

        double totalRevenue = byPlacement.sterms().buckets().array().stream()
                .map(bucket -> bucket.aggregations().get("total_revenue"))
                .filter(agg -> agg != null && agg.isSum() && agg.sum().value() != null)
                .mapToDouble(agg -> agg.sum().value())
                .sum();
        if (totalRevenue <= 0D) {
            return rows;
        }

        for (var bucket : byPlacement.sterms().buckets().array()) {
            double revenue = 0D;
            var totalRevenueAgg = bucket.aggregations().get("total_revenue");
            if (totalRevenueAgg != null && totalRevenueAgg.isSum() && totalRevenueAgg.sum().value() != null) {
                revenue = totalRevenueAgg.sum().value();
            }
            double ratio = revenue / totalRevenue;
            rows.add(new IapPlacementRatioDTO(bucket.key().stringValue(), revenue, ratio));
        }

        return rows;
    }

    private Query buildChartQuery(SearchFilters filters, String gameVersionField) {
        final List<Query> filterQueries = new ArrayList<>();
        final List<Query> shouldQueries = new ArrayList<>();

        addMultiValueExactFilter(filterQueries, gameVersionField, filters.getGameVersion());
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

        if (hasText(filters.getTerm())) {
            shouldQueries.add(Query.of(q -> q.match(m -> m.field("userId").query(filters.getTerm()))));
            shouldQueries.add(Query.of(q -> q.match(m -> m.field("productId").query(filters.getTerm()))));
            shouldQueries.add(Query.of(q -> q.match(m -> m.field("transactionId").query(filters.getTerm()))));
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

    private static List<String> normalizeValues(final List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }

        return values.stream()
                .filter(IapServiceImpl::hasText)
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());
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
