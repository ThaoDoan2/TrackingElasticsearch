package com.example.rest_service.service.index;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.endpoints.BooleanResponse;

@Service
public class IndexService {
    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);

    private final ElasticsearchClient client;

    private final ResourceLoader resourceLoader;

    public IndexService(ElasticsearchClient client, ResourceLoader resourceLoader) {
        this.client = client;
        this.resourceLoader = resourceLoader;
    }

    public void createIndices() {
        final List<IndexInfo> indexInformation = getIndexInformation();

        if (CollectionUtils.isEmpty(indexInformation)) {
            LOG.info("No index information found. Skipping index creation.");
            return;
        }

        for (final IndexInfo indexInfo : indexInformation) {
            delete(indexInfo);
            create(indexInfo);
        }
    }

    private void create(IndexInfo indexInfo) {
        LOG.info("Creating index {}", indexInfo.name());
        try {
            client.indices().create(r -> r.index(indexInfo.name())
                    .settings(s -> s.withJson(getAsInputStream("static/setting.json")))
                    .mappings(t -> t.withJson(getAsInputStream(indexInfo.mappingPath()))));
        } catch (Exception e) {
            LOG.error("Error during index creation for index {}: {}", indexInfo.name(), e.getMessage(), e);
        }
    }

    private void delete(IndexInfo indexInfo) {
        try {
            final BooleanResponse exist = client.indices().exists(r -> r.index(indexInfo.name()));
            if (!exist.value()) {
                LOG.info("Index {} does not exist. Skipping deletion.", indexInfo.name());
                return;
            }
            client.indices().delete(r -> r.index(indexInfo.name()));
        } catch (Exception e) {
            LOG.error("Error during index deletion for index {}: {}",
                    indexInfo.name(), e.getMessage(), e);
        }
    }

    private List<IndexInfo> getIndexInformation() {
        final var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Document.class));

        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents("com.example.rest_service");

        return beanDefinitions.stream()
                .map(IndexService::getIndexInfo)
                .filter(Objects::nonNull)
                .toList();
    }

    private static IndexInfo getIndexInfo(final BeanDefinition beanDefinition) {
        try {
            Class<?> documentClass = Class.forName(beanDefinition.getBeanClassName());

            return new IndexInfo(getIndexName(documentClass),
                    getIndexMappingPath(documentClass));
        } catch (ClassNotFoundException e) {
            LOG.error("Error during retrieving index name for bean {}: {}",
                    beanDefinition.getBeanClassName(), e.getMessage(), e);
        }
        return null;
    }

    private static String getIndexName(final Class<?> documentClass) {
        final Document annotation = documentClass.getAnnotation(Document.class);
        return annotation.indexName();
    }

    private static String getIndexMappingPath(final Class<?> documentClass) {
        final Mapping annotation = documentClass.getAnnotation(Mapping.class);
        return annotation.mappingPath();
    }

    private InputStream getAsInputStream(final String jsonFilePath) {
        try {
            final Resource resource = resourceLoader.getResource("classpath:" + jsonFilePath);
            return resource.getInputStream();
        } catch (Exception e) {
            LOG.error("Error during loading mapping from path {}: {}",
                    jsonFilePath, e.getMessage(), e);
            return null;
        }
    }

}
