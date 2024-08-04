package com.data.processing.elastic.query.client.service.impl;

import com.data.processing.common.utils.CollectionsUtil;
import com.data.processing.elastic.model.index.impl.SourceIndexModel;
import com.data.processing.elastic.query.client.exceptions.ElasticQueryClientException;
import com.data.processing.elastic.query.client.repository.SourceElasticsearchQueryRepository;
import com.data.processing.elastic.query.client.service.ElasticQueryClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class SourceElasticRepositoryQueryClient implements ElasticQueryClient<SourceIndexModel> {

    private final SourceElasticsearchQueryRepository sourceElasticsearchQueryRepository;


    @Override
    public SourceIndexModel getIndexModelById(String id) {
        Optional<SourceIndexModel> searchResult = sourceElasticsearchQueryRepository.findById(id);
        log.info("Document with id {} retrieved successfully",
                searchResult.orElseThrow(() ->
                        new ElasticQueryClientException("No document found at elasticsearch with id: " + id)).getId());
        return searchResult.get();
    }

    @Override
    public List<SourceIndexModel> getIndexModelByText(String text) {
        List<SourceIndexModel> searchResult = sourceElasticsearchQueryRepository.findByText(text);
        log.info("{} of documents with text {} retrieved successfully", searchResult.size(), text);
        return searchResult;
    }

    @Override
    public List<SourceIndexModel> getAllIndexModels() {
        List<SourceIndexModel> searchResult =
                CollectionsUtil.getInstance().getListFromIterable(sourceElasticsearchQueryRepository.findAll());
        log.info("{} number of documents retrieved successfully", searchResult.size());
        return searchResult;
    }

}
