package com.example.rest_service.feature.gameplay.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.rest_service.feature.gameplay.dto.GamePlayDTO;
import com.example.rest_service.feature.gameplay.dto.GamePlayLoseByLevelDTO;
import com.example.rest_service.feature.gameplay.dto.GamePlayStartByLevelDTO;
import com.example.rest_service.feature.gameplay.dto.GamePlayWinByLevelDTO;
import com.example.rest_service.feature.gameplay.repository.GamePlayDocument;
import com.example.rest_service.feature.gameplay.repository.GamePlayRepository;
import com.example.rest_service.feature.gameplay.service.converter.GamePlayDTOConverter;
import com.example.rest_service.feature.user.service.UserAccountService;
import com.example.rest_service.search.ElasticsearchProxy;
import com.example.rest_service.search.SearchFilters;
import com.example.rest_service.search.query.QueryType;
import com.example.rest_service.search.query.SearchMeta;
import com.example.rest_service.service.support.AbstractElasticsearchAggregationService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.util.NamedValue;

@Service
public class GamePlayServiceImpl extends AbstractElasticsearchAggregationService implements IGamePlayService {
    private static final Logger LOG = LoggerFactory.getLogger(GamePlayServiceImpl.class);
    private static final String INDEX = "gameplay";

    private final GamePlayRepository repository;
    private final GamePlayDTOConverter converter;
    private final ElasticsearchProxy<GamePlayDocument, GamePlayDTO> client;
    private final UserAccountService userAccountService;
    public GamePlayServiceImpl(GamePlayRepository repository,
            GamePlayDTOConverter converter,
            ElasticsearchProxy<GamePlayDocument, GamePlayDTO> client,
            ElasticsearchClient elasticsearchClient,
            UserAccountService userAccountService) {
        super(elasticsearchClient);
        this.repository = repository;
        this.converter = converter;
        this.client = client;
        this.userAccountService = userAccountService;
    }

    @Override
    public void save(GamePlayDocument gameplay) {
        repository.save(gameplay);
    }

    @Override
    public void save(GamePlayDTO gameplay) {
        repository.save(converter.convertToDocument(gameplay));
    }

    @Override
    public List<GamePlayDTO> search(SearchFilters filters) {
        userAccountService.applyGameScope(filters);
        return client.search(
                filters,
                new SearchMeta(List.of("userId", "gameId", "eventType"), INDEX, QueryType.MATCH),
                GamePlayDocument.class);
    }

    @Override
    public List<GamePlayWinByLevelDTO> totalWinsByLevel(SearchFilters filters) {
        userAccountService.applyGameScope(filters);
        try {
            return executeWinsByLevelAggregation(filters);
        } catch (Exception error) {
            LOG.error("Gameplay wins-by-level query failed. Root cause: {}", error.getMessage(), error);
            return List.of();
        }
    }

    @Override
    public List<GamePlayStartByLevelDTO> totalStartsByLevel(SearchFilters filters) {
        userAccountService.applyGameScope(filters);
        try {
            return executeStartsByLevelAggregation(filters);
        } catch (Exception error) {
            LOG.error("Gameplay starts-by-level query failed. Root cause: {}", error.getMessage(), error);
            return List.of();
        }
    }

    @Override
    public List<GamePlayLoseByLevelDTO> totalLosesByLevel(SearchFilters filters) {
        userAccountService.applyGameScope(filters);
        try {
            return executeLosesByLevelAggregation(filters);
        } catch (Exception error) {
            LOG.error("Gameplay loses-by-level query failed. Root cause: {}", error.getMessage(), error);
            return List.of();
        }
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
    public List<String> getAllPlatforms() {
        return getDistinctFieldValues("platform");
    }

    private List<GamePlayWinByLevelDTO> executeWinsByLevelAggregation(SearchFilters filters) throws Exception {
        final Query query = buildWinsChartQuery(filters);
        final SearchResponse<Void> response = elasticsearchClient.search(s -> s
                .index(INDEX)
                .size(0)
                .query(query)
                .aggregations("by_level", a -> a
                        .terms(t -> t
                                .field("gameLevel")
                                .size(1000)
                                .order(NamedValue.of("_key", SortOrder.Asc))
                            .minDocCount(1))
                        .aggregations("unique_users", sub -> sub
                                .cardinality(c -> c.field("userId")))
                        .aggregations("total_duration", sub -> sub
                                .avg(avg -> avg.field("duration")))),
                Void.class);

        final Aggregate byLevel = response.aggregations().get("by_level");
        if (byLevel == null) {
            return List.of();
        }

        final Map<Long, GamePlayWinByLevelDTO> valuesByLevel = new TreeMap<>();
        if (byLevel.isLterms()) {
            for (var bucket : byLevel.lterms().buckets().array()) {
                final Long level = bucket.key();
                final Long totalWins = bucket.docCount();
                final Long totalUsersWin = extractCardinality(bucket.aggregations().get("unique_users"));
                final Double duration = extractAvg(bucket.aggregations().get("total_duration"));
                valuesByLevel.put(level, new GamePlayWinByLevelDTO(level, totalWins, totalUsersWin, duration));
            }
        } else if (byLevel.isSterms()) {
            for (var bucket : byLevel.sterms().buckets().array()) {
                final Long level = parseLong(bucket.key().stringValue());
                if (level == null) {
                    continue;
                }
                final Long totalWins = bucket.docCount();
                final Long totalUsersWin = extractCardinality(bucket.aggregations().get("unique_users"));
                final Double duration = extractAvg(bucket.aggregations().get("total_duration"));
                valuesByLevel.put(level, new GamePlayWinByLevelDTO(level, totalWins, totalUsersWin, duration));
            }
        }

        if (valuesByLevel.isEmpty()) {
            return List.of();
        }

        long fromLevel = valuesByLevel.keySet().stream().mapToLong(Long::longValue).min().orElse(0L);
        long toLevel = valuesByLevel.keySet().stream().mapToLong(Long::longValue).max().orElse(fromLevel);
        if (filters.getMinLevel() != null) {
            fromLevel = filters.getMinLevel().longValue();
        }
        if (filters.getMaxLevel() != null) {
            toLevel = filters.getMaxLevel().longValue();
        }
        if (toLevel < fromLevel) {
            final long temp = fromLevel;
            fromLevel = toLevel;
            toLevel = temp;
        }

        final List<GamePlayWinByLevelDTO> result = new ArrayList<>();
        for (long level = fromLevel; level <= toLevel; level++) {
            final GamePlayWinByLevelDTO value = valuesByLevel.get(level);
            if (value != null) {
                result.add(value);
            }
        }
        return result;
    }

    private Query buildWinsChartQuery(final SearchFilters filters) {
        return buildStatusChartQuery(filters, List.of("win", "Win", "WIN"));
    }

    private List<GamePlayStartByLevelDTO> executeStartsByLevelAggregation(SearchFilters filters) throws Exception {
        final Query query = buildStatusChartQuery(filters, List.of("start", "Start", "START"));
        final SearchResponse<Void> response = elasticsearchClient.search(s -> s
                .index(INDEX)
                .size(0)
                .query(query)
                .aggregations("by_level", a -> a
                        .terms(t -> t
                                .field("gameLevel")
                                .size(1000)
                                .order(NamedValue.of("_key", SortOrder.Asc)))
                        .aggregations("unique_users", sub -> sub
                                .cardinality(c -> c.field("userId")))
                        .aggregations("total_duration", sub -> sub
                                .avg(avg -> avg.field("duration")))),
                Void.class);

        final Aggregate byLevel = response.aggregations().get("by_level");
        if (byLevel == null) {
            return List.of();
        }

        final Map<Long, GamePlayStartByLevelDTO> valuesByLevel = new TreeMap<>();
        if (byLevel.isLterms()) {
            for (var bucket : byLevel.lterms().buckets().array()) {
                final Long level = bucket.key();
                final Long totalStarts = bucket.docCount();
                final Long totalUsersStart = extractCardinality(bucket.aggregations().get("unique_users"));
                valuesByLevel.put(level, new GamePlayStartByLevelDTO(level, totalStarts, totalUsersStart));
            }
        } else if (byLevel.isSterms()) {
            for (var bucket : byLevel.sterms().buckets().array()) {
                final Long level = parseLong(bucket.key().stringValue());
                if (level == null) {
                    continue;
                }
                final Long totalStarts = bucket.docCount();
                final Long totalUsersStart = extractCardinality(bucket.aggregations().get("unique_users"));
                valuesByLevel.put(level, new GamePlayStartByLevelDTO(level, totalStarts, totalUsersStart));
            }
        }

        if (valuesByLevel.isEmpty()) {
            return List.of();
        }

        long fromLevel = valuesByLevel.keySet().stream().mapToLong(Long::longValue).min().orElse(0L);
        long toLevel = valuesByLevel.keySet().stream().mapToLong(Long::longValue).max().orElse(fromLevel);
        if (filters.getMinLevel() != null) {
            fromLevel = filters.getMinLevel().longValue();
        }
        if (filters.getMaxLevel() != null) {
            toLevel = filters.getMaxLevel().longValue();
        }
        if (toLevel < fromLevel) {
            final long temp = fromLevel;
            fromLevel = toLevel;
            toLevel = temp;
        }

        final List<GamePlayStartByLevelDTO> result = new ArrayList<>();
        for (long level = fromLevel; level <= toLevel; level++) {
            final GamePlayStartByLevelDTO value = valuesByLevel.get(level);
            if (value != null) {
                result.add(value);
            }
        }
        return result;
    }

    private List<GamePlayLoseByLevelDTO> executeLosesByLevelAggregation(SearchFilters filters) throws Exception {
        final Query query = buildStatusChartQuery(filters, List.of("lose", "Lose", "LOSE"));
        final SearchResponse<Void> response = elasticsearchClient.search(s -> s
                .index(INDEX)
                .size(0)
                .query(query)
                .aggregations("by_level", a -> a
                        .terms(t -> t
                                .field("gameLevel")
                                .size(1000)
                                .order(NamedValue.of("_key", SortOrder.Asc)))
                        .aggregations("unique_users", sub -> sub
                                .cardinality(c -> c.field("userId")))
                        .aggregations("total_duration", sub -> sub
                                .avg(avg -> avg.field("duration")))),
                Void.class);

        final Aggregate byLevel = response.aggregations().get("by_level");
        if (byLevel == null) {
            return List.of();
        }

        final Map<Long, GamePlayLoseByLevelDTO> valuesByLevel = new TreeMap<>();
        if (byLevel.isLterms()) {
            for (var bucket : byLevel.lterms().buckets().array()) {
                final Long level = bucket.key();
                final Long totalLoses = bucket.docCount();
                final Long totalUsersLose = extractCardinality(bucket.aggregations().get("unique_users"));
                final Double duration = extractAvg(bucket.aggregations().get("total_duration"));
                valuesByLevel.put(level, new GamePlayLoseByLevelDTO(level, totalLoses, totalUsersLose, duration));
            }
        } else if (byLevel.isSterms()) {
            for (var bucket : byLevel.sterms().buckets().array()) {
                final Long level = parseLong(bucket.key().stringValue());
                if (level == null) {
                    continue;
                }
                final Long totalLoses = bucket.docCount();
                final Long totalUsersLose = extractCardinality(bucket.aggregations().get("unique_users"));
                final Double duration = extractAvg(bucket.aggregations().get("total_duration"));
                valuesByLevel.put(level, new GamePlayLoseByLevelDTO(level, totalLoses, totalUsersLose, duration));
            }
        }

        if (valuesByLevel.isEmpty()) {
            return List.of();
        }

        long fromLevel = valuesByLevel.keySet().stream().mapToLong(Long::longValue).min().orElse(0L);
        long toLevel = valuesByLevel.keySet().stream().mapToLong(Long::longValue).max().orElse(fromLevel);
        if (filters.getMinLevel() != null) {
            fromLevel = filters.getMinLevel().longValue();
        }
        if (filters.getMaxLevel() != null) {
            toLevel = filters.getMaxLevel().longValue();
        }
        if (toLevel < fromLevel) {
            final long temp = fromLevel;
            fromLevel = toLevel;
            toLevel = temp;
        }

        final List<GamePlayLoseByLevelDTO> result = new ArrayList<>();
        for (long level = fromLevel; level <= toLevel; level++) {
            final GamePlayLoseByLevelDTO value = valuesByLevel.get(level);
            if (value != null) {
                result.add(value);
            }
        }
        return result;
    }

    private Query buildStatusChartQuery(final SearchFilters filters, final List<String> statuses) {
        final List<Query> filterQueries = new ArrayList<>();
        final List<Query> shouldQueries = new ArrayList<>();

        addMultiValueExactFilter(filterQueries, "gameVersion", filters.getGameVersion());
        addMultiValueExactFilter(filterQueries, "gameId", filters.getGameIds());
        addMultiValueExactFilter(filterQueries, "country", filters.getCountryCode());
        addMultiValueExactFilter(filterQueries, "platform", filters.getPlatform());
        addMultiValueExactFilter(filterQueries, "status", statuses);

        if (filters.getMinLevel() != null || filters.getMaxLevel() != null) {
            filterQueries.add(Query.of(q -> q.range(r -> r.number(n -> {
                n.field("gameLevel");
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
            shouldQueries.add(Query.of(q -> q.match(m -> m.field("eventType").query(filters.getTerm()))));
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

    @Override
    public void initData(){
        String countries[] = {"US", "UK", "DE", "FR", "JP"};
        String gameModes[] = {"Classic", "Time Attack", "Survival"};

        for (int i = 0; i < 100; i ++ ){
            int r = new Random().nextInt(100);
            GamePlayDocument doc = new GamePlayDocument();
            doc.setUserId("user" + (r % 50));
            doc.setGameId("com.higame.goods.sorting.match.triple.master");
            doc.setEventType("PlayLevel");
            doc.setPlatform(r % 2 == 0 ? "Android" : "iOS");
            doc.setCountry(countries[r % countries.length]);
            doc.setGameVersion("1.0.0");
            doc.setLoggedDay((long)r %7);
            Date now = new Date();
            Date accountCreatedDate = new Date(now.getTime() - r * 86400 * 1000L);
            doc.setAccountCreatedDate(accountCreatedDate);
            Date recordDate = new Date(now.getTime() - (r % 7) * 86400 * 1000L);
            doc.setDate(recordDate);
            
            doc.setDuration((long)r * 10);
            doc.setGameMode(gameModes[r % gameModes.length]);
            doc.setGameLevel((long) (i % 5));
            doc.setDifficulty((long) (r % 3));
            doc.setStatus("Start");
            doc.setCompletion(0L);


            repository.save(doc);
        }

        LOG.info("Sample data initialized");
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

    private static Long extractCardinality(final Aggregate aggregate) {
        if (aggregate != null && aggregate.isCardinality()) {
            return aggregate.cardinality().value();
        }
        return 0L;
    }

    private static Double extractAvg(final Aggregate aggregate) {
        if (aggregate == null) {
            return 0D;
        }
        if (aggregate.isAvg() && aggregate.avg().value() != null) {
            return aggregate.avg().value();
        }
        if (aggregate.isSimpleValue() && aggregate.simpleValue().value() != null) {
            return aggregate.simpleValue().value();
        }
        return 0D;
    }



    private List<String> getDistinctFieldValues(final String fieldName) {
        return getDistinctFieldValuesWithKeywordFallback(
                LOG,
                INDEX,
                fieldName,
                userAccountService.getCurrentUserGameScopeOrEmptyForAdmin());
    }
}
