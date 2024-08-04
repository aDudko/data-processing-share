package com.data.processing.reactive.elastic.query.web.client.service.impl;

import com.data.processing.common.query.web.client.exceprion.ElasticQueryWebClientException;
import com.data.processing.common.query.web.client.model.ElasticQueryWebClientRequestModel;
import com.data.processing.common.query.web.client.model.ElasticQueryWebClientResponseModel;
import com.data.processing.config.ElasticQueryWebClientConfigData;
import com.data.processing.reactive.elastic.query.web.client.service.ElasticQueryWebClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ElasticQueryWebClientImpl implements ElasticQueryWebClient {

    private final WebClient webClient;
    private final ElasticQueryWebClientConfigData elasticQueryWebClientConfig;

    public ElasticQueryWebClientImpl(
            @Qualifier("webClient") WebClient client,
            ElasticQueryWebClientConfigData configData) {
        this.webClient = client;
        this.elasticQueryWebClientConfig = configData;
    }


    @Override
    public Flux<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel) {
        log.info("Querying by text {}", requestModel.getText());
        return getWebClient(requestModel).bodyToFlux(ElasticQueryWebClientResponseModel.class);
    }


    private WebClient.ResponseSpec getWebClient(ElasticQueryWebClientRequestModel requestModel) {
        return webClient
                .method(HttpMethod.valueOf(elasticQueryWebClientConfig.getQueryByText().getMethod()))
                .uri(elasticQueryWebClientConfig.getQueryByText().getUri())
                .accept(MediaType.valueOf(elasticQueryWebClientConfig.getQueryByText().getAccept()))
                .body(BodyInserters.fromPublisher(Mono.just(requestModel), createParameterizedTypeReference()))
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.equals(HttpStatus.UNAUTHORIZED),
                        clientResponse -> Mono.just(new BadCredentialsException("Not authenticated!")))
                .onStatus(
                        s -> s.equals(HttpStatus.BAD_REQUEST),
                        clientResponse -> Mono.just(
                                new ElasticQueryWebClientException(clientResponse.statusCode().toString())))
                .onStatus(
                        s -> s.equals(HttpStatus.INTERNAL_SERVER_ERROR),
                        clientResponse -> Mono.just(new Exception(clientResponse.statusCode().toString())));
    }

    private <T> ParameterizedTypeReference<T> createParameterizedTypeReference() {
        return new ParameterizedTypeReference<>() {
        };
    }

}
