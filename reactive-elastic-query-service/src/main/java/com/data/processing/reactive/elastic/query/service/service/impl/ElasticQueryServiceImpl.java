package com.data.processing.reactive.elastic.query.service.service.impl;

import com.data.processing.common.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.data.processing.common.elastic.query.service.transformer.ElasticToResponseModelTransformer;
import com.data.processing.elastic.model.index.impl.SourceIndexModel;
import com.data.processing.reactive.elastic.query.service.service.ElasticQueryService;
import com.data.processing.reactive.elastic.query.service.service.ReactiveElasticQueryClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticQueryServiceImpl implements ElasticQueryService {

    private final ReactiveElasticQueryClient<SourceIndexModel> reactiveElasticQueryClient;
    private final ElasticToResponseModelTransformer elasticToResponseModelTransformer;


    @Override
    public Flux<ElasticQueryServiceResponseModel> getDocumentByText(String text) {
        log.info("Querying reactive elasticsearch for text {}", text);
        return reactiveElasticQueryClient
                .getIndexModelByText(text)
                .map(elasticToResponseModelTransformer::getResponseModel);
    }

}
