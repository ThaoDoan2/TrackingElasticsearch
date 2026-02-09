package com.example.rest_service.search;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.rest_service.dto.BaseDTO;
import com.example.rest_service.repository.AbstractDocument;
import com.example.rest_service.search.query.QueryBuilder;
import com.example.rest_service.search.query.SearchMeta;
import com.example.rest_service.service.converter.IConverter;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;

public class ElasticsearchProxy <E extends  AbstractDocument, T extends BaseDTO> {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchProxy.class);

    private final Map<Class<E>, IConverter<E, T>> CONVERTER_MAP = new HashMap<>(10);

    private ElasticsearchClient client;

    private List<IConverter<E, T>> converters;

    public ElasticsearchProxy(ElasticsearchClient client, List<IConverter<E, T>> converters) {
        this.client = client;
        this.converters = converters;

        for (IConverter<E, T> converter : converters) {
            CONVERTER_MAP.put(converter.getDocumentClass(), converter);
        }
    }

    public List<T> search(final SearchFilters filters, final SearchMeta meta, final Class<E> documentClass) {

        try {
            SearchResponse<E> response = client.search(
                QueryBuilder.buildSearchRequest(filters, meta),
                documentClass
            );

            List<E> documents = response.hits().hits().stream()
                .map(hit -> hit.source())
                .toList();

            IConverter<E, T> converter = CONVERTER_MAP.get(documentClass);

            return documents.stream()
                .map(converter::convertToDto)
                .toList();
                
        } catch (IOException e) {
            LOG.error("Error during Elasticsearch search {}", e.getMessage(), e);

            return List.of();
        }
    }
}
