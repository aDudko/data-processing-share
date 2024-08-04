package com.data.processing.reactive.elastic.query.service.repository;

import com.data.processing.elastic.model.index.impl.SourceIndexModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ElasticQueryRepository extends ReactiveCrudRepository<SourceIndexModel, String> {

    Flux<SourceIndexModel> findByText(String text);

}
