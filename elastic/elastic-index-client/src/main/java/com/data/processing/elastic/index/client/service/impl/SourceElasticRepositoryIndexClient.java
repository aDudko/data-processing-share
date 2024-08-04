package com.data.processing.elastic.index.client.service.impl;

import com.data.processing.elastic.index.client.repository.SourceElasticsearchIndexRepository;
import com.data.processing.elastic.index.client.service.ElasticIndexClient;
import com.data.processing.elastic.model.index.impl.SourceIndexModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@ConditionalOnProperty(name = "elastic-config.is-repository", havingValue = "true", matchIfMissing = true)
public class SourceElasticRepositoryIndexClient implements ElasticIndexClient<SourceIndexModel> {

    private final SourceElasticsearchIndexRepository sourceElasticsearchIndexRepository;

    public SourceElasticRepositoryIndexClient(
            SourceElasticsearchIndexRepository indexRepository) {
        this.sourceElasticsearchIndexRepository = indexRepository;
    }


    @Override
    public List<String> save(List<SourceIndexModel> documents) {
        List<SourceIndexModel> repositoryResponse =
                (List<SourceIndexModel>) sourceElasticsearchIndexRepository.saveAll(documents);
        List<String> ids = repositoryResponse.stream()
                .map(SourceIndexModel::getId)
                .collect(Collectors.toList());
        log.info("Documents indexed successfully with type: {} and ids: {}",
                SourceIndexModel.class.getName(), ids);
        return ids;
    }

}
