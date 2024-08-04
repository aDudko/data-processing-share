package com.data.processing.reactive.elastic.query.web.client.service;

import com.data.processing.common.query.web.client.model.ElasticQueryWebClientRequestModel;
import com.data.processing.common.query.web.client.model.ElasticQueryWebClientResponseModel;
import reactor.core.publisher.Flux;

public interface ElasticQueryWebClient {

    Flux<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel request);

}
