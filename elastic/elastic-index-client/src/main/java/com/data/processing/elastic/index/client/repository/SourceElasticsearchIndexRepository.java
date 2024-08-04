package com.data.processing.elastic.index.client.repository;

import com.data.processing.elastic.model.index.impl.SourceIndexModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceElasticsearchIndexRepository extends ElasticsearchRepository<SourceIndexModel, String> {
}
