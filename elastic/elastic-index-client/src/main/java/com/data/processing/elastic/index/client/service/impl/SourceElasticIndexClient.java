package com.data.processing.elastic.index.client.service.impl;

import com.data.processing.config.ElasticConfigData;
import com.data.processing.elastic.index.client.service.ElasticIndexClient;
import com.data.processing.elastic.index.client.utils.ElasticIndexUtil;
import com.data.processing.elastic.model.index.impl.SourceIndexModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "elastic-config.is-repository", havingValue = "false")
public class SourceElasticIndexClient implements ElasticIndexClient<SourceIndexModel> {

    private final ElasticConfigData elasticConfigData;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticIndexUtil<SourceIndexModel> elasticIndexUtil;


    @Override
    public List<String> save(List<SourceIndexModel> documents) {
        List<IndexQuery> indexQueries = elasticIndexUtil.getIndexQueries(documents);
        List<String> documentIds = elasticsearchOperations.bulkIndex(
                indexQueries,
                IndexCoordinates.of(elasticConfigData.getIndexName())
        ).stream().map(IndexedObjectInformation::id).collect(Collectors.toList());
        log.info("Documents indexed successfully with type: {} and ids: {}", SourceIndexModel.class.getName(),
                documentIds);
        return documentIds;
    }

}
