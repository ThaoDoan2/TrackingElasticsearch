package com.example.rest_service.service.index;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.endpoints.BooleanResponse;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.util.CollectionUtils;

@Service
public class IndexService {
    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);

    private ElasticsearchClient client;

    public IndexService(ElasticsearchClient client) {
        this.client = client;
    }

    public void createIndices() {
        final List<IndexInfo> indexInformation = getIndexInformation();
        
        if (CollectionUtils.isEmpty(indexInformation)) {
            LOG.info("No index information found. Skipping index creation.");
            return;
        }

        for (final IndexInfo indexInfo: indexInformation) {
            delete(indexInfo);
            create(indexInfo);
        }
    }

    private void create(IndexInfo indexInfo) {
        try {
            client.indices().create(r -> r.index(indexInfo.indexName()));
        } catch (Exception e) {
            LOG.error("Error during index creation for index {}: {}", indexInfo.indexName(), e.getMessage(), e);
        }
    }

    private void delete(IndexInfo indexInfo) {
        try {
            final BooleanResponse exist = client.indices().exists(r -> r.index(indexInfo.indexName()));
            if (!exist.value()) {
                LOG.info("Index {} does not exist. Skipping deletion.", indexInfo.indexName());
                return;
            }
            client.indices().delete(r -> r.index(indexInfo.indexName()));
        } catch (Exception e) {
            LOG.error("Error during index deletion for index {}: {}",
                    indexInfo.indexName(), e.getMessage(), e);
        }
    }

    private List<IndexInfo> getIndexInformation() {
        final var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Document.class));

        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents("com.example.rest_service");

        return beanDefinitions.stream()
                .map(IndexService::getIndexName)
                .filter(Objects::nonNull)
                .map(IndexInfo::new)
                .toList();
    }

    private static String getIndexName(final BeanDefinition beanDefinition) {
        try {
            Class<?> documentClass = Class.forName(beanDefinition.getBeanClassName());

            final Document annotation = documentClass.getAnnotation(Document.class);
            return annotation.indexName();
        } catch (ClassNotFoundException e) {
            LOG.error("Error during retrieving index name for bean {}: {}",
                    beanDefinition.getBeanClassName(), e.getMessage(), e);
        }
        return null;
    }
}
