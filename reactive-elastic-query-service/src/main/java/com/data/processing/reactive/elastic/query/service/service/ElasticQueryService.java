package com.data.processing.reactive.elastic.query.service.service;

import com.data.processing.common.elastic.query.service.model.ElasticQueryServiceResponseModel;
import reactor.core.publisher.Flux;

public interface ElasticQueryService {

    Flux<ElasticQueryServiceResponseModel> getDocumentByText(String text);

}
