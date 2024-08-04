package com.data.processing.elastic.query.service.service.impl;

import com.data.processing.common.elastic.query.service.exception.ElasticQueryServiceException;
import com.data.processing.common.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.data.processing.config.ElasticQueryServiceConfigData;
import com.data.processing.elastic.model.index.impl.SourceIndexModel;
import com.data.processing.elastic.query.client.service.ElasticQueryClient;
import com.data.processing.elastic.query.service.QueryType;
import com.data.processing.elastic.query.service.model.ElasticQueryServiceAnalyticsResponseModel;
import com.data.processing.elastic.query.service.model.ElasticQueryServiceWordCountResponseModel;
import com.data.processing.elastic.query.service.model.assembler.ElasticQueryServiceResponseModelAssembler;
import com.data.processing.elastic.query.service.service.ElasticQueryService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.data.processing.mdc.interceptor.Constants.CORRELATION_ID_HEADER;
import static com.data.processing.mdc.interceptor.Constants.CORRELATION_ID_KEY;

@Slf4j
@Service
public class SourceElasticQueryService implements ElasticQueryService {

    private final ElasticQueryServiceResponseModelAssembler elasticQueryServiceResponseModelAssembler;
    private final ElasticQueryClient<SourceIndexModel> elasticQueryClient;
    private final ElasticQueryServiceConfigData elasticQueryServiceConfigData;
    private final WebClient.Builder webClientBuilder;

    public SourceElasticQueryService(
            ElasticQueryServiceResponseModelAssembler assembler,
            ElasticQueryClient<SourceIndexModel> queryClient,
            ElasticQueryServiceConfigData queryServiceConfigData,
            @Qualifier("webClientBuilder") WebClient.Builder clientBuilder) {
        this.elasticQueryServiceResponseModelAssembler = assembler;
        this.elasticQueryClient = queryClient;
        this.elasticQueryServiceConfigData = queryServiceConfigData;
        this.webClientBuilder = clientBuilder;
    }


    @Override
    public ElasticQueryServiceResponseModel getDocumentById(String id) {
        log.info("Querying elasticsearch by id: {}", id);
        return elasticQueryServiceResponseModelAssembler.toModel(elasticQueryClient.getIndexModelById(id));
    }

    @Override
    public ElasticQueryServiceAnalyticsResponseModel getDocumentByText(String text, String accessToken) {
        log.info("Querying elasticsearch by text: {}", text);
        List<ElasticQueryServiceResponseModel> elasticQueryServiceResponseModels =
                elasticQueryServiceResponseModelAssembler.toModels(elasticQueryClient.getIndexModelByText(text));
        return ElasticQueryServiceAnalyticsResponseModel.builder()
                .queryResponseModels(elasticQueryServiceResponseModels)
                .wordCount(getWordCount(text, accessToken))
                .build();
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getAllDocuments() {
        log.info("Querying all documents in elasticsearch");
        return elasticQueryServiceResponseModelAssembler.toModels(elasticQueryClient.getAllIndexModels());
    }


    private Long getWordCount(String text, String accessToken) {
        if (QueryType.KAFKA_STATE_STORE.getType().equals(elasticQueryServiceConfigData.getWebClient().getQueryType())) {
            return getFromKafkaStateStore(text, accessToken).getWordCount();
        } else if (QueryType.ANALYTICS_DATABASE.getType().
                equals(elasticQueryServiceConfigData.getWebClient().getQueryType())) {
            return getFromAnayticsDatabase(text, accessToken).getWordCount();
        }
        return 0L;
    }

    private ElasticQueryServiceWordCountResponseModel getFromAnayticsDatabase(String text, String accessToken) {
        ElasticQueryServiceConfigData.Query queryFromAnalyticsDatabase =
                elasticQueryServiceConfigData.getQueryFromAnalyticsDatabase();
        return retrieveResponseModel(text, accessToken, queryFromAnalyticsDatabase);
    }

    private ElasticQueryServiceWordCountResponseModel getFromKafkaStateStore(String text, String accessToken) {
        ElasticQueryServiceConfigData.Query queryFromKafkaStateStore =
                elasticQueryServiceConfigData.getQueryFromKafkaStateStore();
        return retrieveResponseModel(text, accessToken, queryFromKafkaStateStore);
    }

    private ElasticQueryServiceWordCountResponseModel retrieveResponseModel(String text,
                                                                            String accessToken,
                                                                            ElasticQueryServiceConfigData.Query query) {
        return webClientBuilder
                .build()
                .method(HttpMethod.valueOf(query.getMethod()))
                .uri(query.getUri(), uriBuilder -> uriBuilder.build(text))
                .headers(h -> {
                    h.setBearerAuth(accessToken);
                    h.set(CORRELATION_ID_HEADER, MDC.get(CORRELATION_ID_KEY));
                })
                .accept(MediaType.valueOf(query.getAccept()))
                .retrieve()
                .onStatus(
                        s -> s.equals(HttpStatus.UNAUTHORIZED),
                        clientResponse -> Mono.just(new BadCredentialsException("Not authenticated")))
                .onStatus(
                        s -> s.equals(HttpStatus.BAD_REQUEST),
                        clientResponse -> Mono.just(new
                                ElasticQueryServiceException(clientResponse.statusCode().toString())))
                .onStatus(
                        s -> s.equals(HttpStatus.INTERNAL_SERVER_ERROR),
                        clientResponse -> Mono.just(new Exception(clientResponse.statusCode().toString())))
                .bodyToMono(ElasticQueryServiceWordCountResponseModel.class)
                .log()
                .block();
    }

}
