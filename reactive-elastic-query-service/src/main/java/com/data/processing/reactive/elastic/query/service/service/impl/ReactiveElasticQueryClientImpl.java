package com.data.processing.reactive.elastic.query.service.service.impl;

import com.data.processing.config.ElasticQueryServiceConfigData;
import com.data.processing.elastic.model.index.impl.SourceIndexModel;
import com.data.processing.reactive.elastic.query.service.repository.ElasticQueryRepository;
import com.data.processing.reactive.elastic.query.service.service.ReactiveElasticQueryClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReactiveElasticQueryClientImpl implements ReactiveElasticQueryClient<SourceIndexModel> {


    private final ElasticQueryRepository elasticQueryRepository;
    private final ElasticQueryServiceConfigData elasticQueryServiceConfigData;


    @Override
    public Flux<SourceIndexModel> getIndexModelByText(String text) {
        log.info("Getting data from elasticsearch for text {}", text);
        return elasticQueryRepository
                .findByText(text)
                .delayElements(Duration.ofMillis(elasticQueryServiceConfigData.getBackPressureDelayMs()));
    }

}
