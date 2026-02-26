package com.example.rest_service.feature.iap.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.rest_service.feature.iap.dto.IapCountPerDayDTO;
import com.example.rest_service.feature.iap.dto.IapDTO;
import com.example.rest_service.feature.iap.dto.IapDailyProductTotalDTO;
import com.example.rest_service.feature.iap.dto.IapFilterOptionsDTO;
import com.example.rest_service.feature.iap.dto.IapPlacementRatioDTO;
import com.example.rest_service.feature.iap.repository.IapDocument;
import com.example.rest_service.feature.iap.repository.IapRepository;
import com.example.rest_service.feature.iap.service.converter.IapDTOConverter;
import com.example.rest_service.feature.user.service.UserAccountService;
import com.example.rest_service.search.ElasticsearchProxy;
import com.example.rest_service.search.SearchFilters;
import com.example.rest_service.search.query.QueryType;
import com.example.rest_service.search.query.SearchMeta;
import com.example.rest_service.service.support.AbstractElasticsearchAggregationService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;

@Service
public class IapServiceImpl extends AbstractElasticsearchAggregationService implements IIapService {
    private static final Logger LOG = LoggerFactory.getLogger(IapServiceImpl.class);
    private static final String INDEX = "iap";

    private final IapRepository repository;
    private final IapDTOConverter converter;
    private final ElasticsearchProxy<IapDocument, IapDTO> client;
    private final UserAccountService userAccountService;

    public IapServiceImpl(IapRepository repository,
            IapDTOConverter converter,
            ElasticsearchProxy<IapDocument, IapDTO> client,
            ElasticsearchClient elasticsearchClient,
            UserAccountService userAccountService) {
        super(elasticsearchClient);
        this.repository = repository;
        this.converter = converter;
        this.client = client;
        this.userAccountService = userAccountService;
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
        userAccountService.applyGameScope(filters);
        return client.search(
                filters,
                new SearchMeta(List.of("userId", "productId", "transactionId"), INDEX, QueryType.MATCH),
                IapDocument.class);
    }

    @Override
    public List<IapCountPerDayDTO> countPerDay(SearchFilters filters) {
        userAccountService.applyGameScope(filters);
        try {
            return executeCountPerDayQuery(filters);
        } catch (IOException error) {
            LOG.error("IAP count per day query failed. Root cause: {}", error.getMessage(), error);
            return List.of();
        }
    }

    @Override
    public List<IapDailyProductTotalDTO> totalRevenuePerDay(SearchFilters filters) {
        userAccountService.applyGameScope(filters);
        try {
            return executeTotalRevenuePerDayQuery(filters);
        } catch (IOException error) {
            LOG.error("IAP total revenue per day query failed. Root cause: {}", error.getMessage(), error);
            return List.of();
        }
    }

    @Override
    public List<IapPlacementRatioDTO> purchaseRatioByPlacement(SearchFilters filters) {
        userAccountService.applyGameScope(filters);
        try {
            return executePurchaseRatioByPlacementQuery(filters, "placement");
        } catch (IOException error) {
            LOG.error("IAP purchase ratio by placement query failed. Root cause: {}", error.getMessage(), error);
            return List.of();
        }
    }

    @Override
    public IapFilterOptionsDTO getFilterOptions() {
        return new IapFilterOptionsDTO(
                getDistinctFieldValues("country"),
                getDistinctFieldValues("productId"),
                getDistinctFieldValues("placement"),
                getDistinctFieldValues("platform"),
                getDistinctFieldValues("gameVersion"));
    }

    @Override
    public List<String> getAllCountries() {
        return getDistinctFieldValues("country");
    }

    @Override
    public List<String> getAllProductIds() {
        return getDistinctFieldValues("productId");
    }

    @Override
    public List<String> getAllPlacements() {
        return getDistinctFieldValues("placement");
    }

    @Override
    public List<String> getAllPlatforms() {
        return getDistinctFieldValues("platform");
    }

    @Override
    public List<String> getAllGameVersions() {
        return getDistinctFieldValues("gameVersion");
    }

    private List<IapCountPerDayDTO> executeCountPerDayQuery(SearchFilters filters)
            throws IOException {
        SearchResponse<Void> response = executeChartAggregationQuery(filters);
        List<IapCountPerDayDTO> rows = new ArrayList<>();

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
            rows.add(new IapCountPerDayDTO(dateBucket.keyAsString(), products));
        }

        return rows;
    }

    private SearchResponse<Void> executeChartAggregationQuery(SearchFilters filters)
            throws IOException {
        final Query query = buildQuery(filters);
        final String PRODUCT_ID = "productId";
        return elasticsearchClient.search(s -> s
                .index(INDEX)
                .size(0)
                .query(query)
                .aggregations("by_date", a -> a
                        .dateHistogram(d -> d
                                .field("date")
                                .calendarInterval(CalendarInterval.Day)
                                .format("yyyy-MM-dd")
                                .minDocCount(1))
                        .aggregations("by_product", sub -> sub
                                .terms(t -> t.field(PRODUCT_ID).size(1000)))),
                Void.class);
    }

    private List<IapDailyProductTotalDTO> executeTotalRevenuePerDayQuery(SearchFilters filters) throws IOException {
        final Query query = buildQuery(filters);
        final String PRODUCT_ID = "productId";
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
                        .aggregations("by_product", sub -> sub
                                .terms(t -> t.field(PRODUCT_ID).size(1000))
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

    private List<IapPlacementRatioDTO> executePurchaseRatioByPlacementQuery(SearchFilters filters,
            String placementField) throws IOException {
        final Query query = buildQuery(filters);
        SearchResponse<Void> response = elasticsearchClient.search(s -> s
                .index(INDEX)
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

    private Query buildQuery(SearchFilters filters) {
        final List<Query> filterQueries = new ArrayList<>();
        final List<Query> shouldQueries = new ArrayList<>();

        addMultiValueExactFilter(filterQueries, "gameVersion", filters.getGameVersion());
        addMultiValueExactFilter(filterQueries, "gameId", filters.getGameIds());
        addMultiValueExactFilter(filterQueries, "country", filters.getCountryCode());
        addMultiValueExactFilter(filterQueries, "platform", filters.getPlatform());
        if (filters.getMinLevel() != null || filters.getMaxLevel() != null) {
            filterQueries.add(Query.of(q -> q.range(r -> r.number(n -> {
                n.field("level");
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

    private List<String> getDistinctFieldValues(final String fieldName) {
        return getDistinctFieldValuesWithKeywordFallback(
                LOG,
                INDEX,
                fieldName,
                userAccountService.getCurrentUserGameScopeOrEmptyForAdmin());
    }

    @Override
    public void initData() {

        String countries[] = { "US", "UK", "DE", "FR", "JP" };
        String gameIds[] = { "com.higame.goods.sorting.match.triple.master",
                "com.higame.brain.twist.tricky.puzzle" };

        String placements[] = { "BattlePass", "Shop", "Offer", "Lose" };
        String productIds[] = { "Starter", "BasicBundle", "BigBundle", "PremiumBundle", "EpicBundle",
                "LegendaryBundle" };
        float prices[] = { 0.99f, 4.99f, 9.99f, 19.99f, 49.99f, 99.99f };

        for (int i = 0; i < 100; i++) {
            int r = new Random().nextInt(1000);
            int r2 = new Random().nextInt(100);
            IapDocument doc = new IapDocument();
            doc.setUserId("user" + (r % 50));
            doc.setGameId(gameIds[r2 % gameIds.length]);
            doc.setEventType("Iap");
            doc.setPlatform(r % 2 == 0 ? "Android" : "iOS");
            doc.setCountry(countries[r % countries.length]);
            doc.setGameVersion("1.0.0");
            doc.setLoggedDay((long) r % 7);
            doc.setLevel((long) r % 137);
            Date now = new Date();
            Date accountCreatedDate = new Date(now.getTime() - r * 86400 * 1000L);
            doc.setAccountCreatedDate(accountCreatedDate);
            Date recordDate = new Date(now.getTime() - (r % 7) * 86400 * 1000L);
            doc.setDate(recordDate);

            doc.setPlacement(placements[r % placements.length]);
            doc.setProductId(productIds[r % productIds.length]);
            doc.setTransactionId("transaction " + i);
            doc.setPrice(prices[r % prices.length]);
            doc.setCurrencyCode("USD");

            repository.save(doc);
        }

        LOG.info("IAP Sample data initialized");
    }
}
