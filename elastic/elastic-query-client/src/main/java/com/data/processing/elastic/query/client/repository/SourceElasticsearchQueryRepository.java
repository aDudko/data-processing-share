package com.data.processing.elastic.query.client.repository;

import com.data.processing.elastic.model.index.impl.SourceIndexModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourceElasticsearchQueryRepository extends ElasticsearchRepository<SourceIndexModel, String> {

    List<SourceIndexModel> findByText(String text);

}
