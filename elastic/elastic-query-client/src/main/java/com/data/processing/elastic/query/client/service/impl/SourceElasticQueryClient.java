package com.data.processing.elastic.query.client.service.impl;

import com.data.processing.config.ElasticConfigData;
import com.data.processing.config.ElasticQueryConfigData;
import com.data.processing.elastic.model.index.impl.SourceIndexModel;
import com.data.processing.elastic.query.client.exceptions.ElasticQueryClientException;
import com.data.processing.elastic.query.client.service.ElasticQueryClient;
import com.data.processing.elastic.query.client.utils.ElasticQueryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SourceElasticQueryClient implements ElasticQueryClient<SourceIndexModel> {

    private final ElasticConfigData elasticConfigData;
    private final ElasticQueryConfigData elasticQueryConfigData;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticQueryUtil<SourceIndexModel> elasticQueryUtil;


    @Override
    public SourceIndexModel getIndexModelById(String id) {
        Query query = elasticQueryUtil.getSearchQueryById(id);
        SearchHit<SourceIndexModel> searchResult = elasticsearchOperations.searchOne(query, SourceIndexModel.class,
                IndexCoordinates.of(elasticConfigData.getIndexName()));
        if (searchResult == null) {
            log.error("No document found at elasticsearch with id: {}", id);
            throw new ElasticQueryClientException("No document found at elasticsearch with id: " + id);
        }
        log.info("Document with id: {} retrieved successfully", searchResult.getId());
        return searchResult.getContent();
    }

    @Override
    public List<SourceIndexModel> getIndexModelByText(String text) {
        Query query = elasticQueryUtil.getSearchQueryByFieldText(elasticQueryConfigData.getTextField(), text);
        return search(query, "{} of documents with text '{}' retrieved successfully", text);
    }

    @Override
    public List<SourceIndexModel> getAllIndexModels() {
        Query query = elasticQueryUtil.getSearchQueryForAll();
        return search(query, "{} number of documents retrieved successfully");
    }


    private List<SourceIndexModel> search(Query query, String logMessage, Object... logParams) {
        SearchHits<SourceIndexModel> searchResult = elasticsearchOperations.search(
                query,
                SourceIndexModel.class,
                IndexCoordinates.of(elasticConfigData.getIndexName()));
        log.info(logMessage, searchResult.getTotalHits(), logParams);
        return searchResult.get()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

}
