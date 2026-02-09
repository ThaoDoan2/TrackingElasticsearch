package com.example.rest_service.search.query;

import com.example.rest_service.search.SearchFilters;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;

public final class QueryBuilder {

    private QueryBuilder(){

    }

    public static SearchRequest buildSearchRequest(final SearchFilters filters, final SearchMeta meta) {
        SearchRequest.Builder builder = new SearchRequest.Builder();
        builder.index(meta.getIndex());

        Query.Builder queryBuilder = new Query.Builder();
        if (meta.getType() == QueryType.MATCH){
            MatchQuery.Builder matchQuery = new MatchQuery.Builder();
            
            for (String field : meta.getFields()) {
                matchQuery.field(field);
            }

            matchQuery.query(filters.getTerm());
            queryBuilder.match(matchQuery.build());

        }

        builder.query(queryBuilder.build());
        return builder.build();
    }


}
