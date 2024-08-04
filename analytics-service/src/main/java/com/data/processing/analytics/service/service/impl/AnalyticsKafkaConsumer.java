package com.data.processing.analytics.service.service.impl;

import com.data.processing.analytics.service.dataaccess.entity.AnalyticsEntity;
import com.data.processing.analytics.service.dataaccess.repository.AnalyticsRepository;
import com.data.processing.analytics.service.service.KafkaConsumer;
import com.data.processing.analytics.service.transformer.AvroToDbEntityModelTransformer;
import com.data.processing.config.KafkaConfigData;
import com.data.processing.kafka.admin.client.KafkaAdminClient;
import com.data.processing.kafka.avro.model.SourceAnalyticsAvroModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsKafkaConsumer implements KafkaConsumer<SourceAnalyticsAvroModel> {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final KafkaAdminClient kafkaAdminClient;
    private final KafkaConfigData kafkaConfig;
    private final AvroToDbEntityModelTransformer avroToDbEntityModelTransformer;
    private final AnalyticsRepository analyticsRepository;


    @EventListener
    public void onAppStarted(ApplicationStartedEvent event) {
        kafkaAdminClient.checkTopicsCreated();
        log.info("Topics with name {} is ready for operations!", kafkaConfig.getTopicNamesToCreate().toArray());
        kafkaListenerEndpointRegistry.getListenerContainer("sourceAnalyticsTopicListener").start();
    }

    @Override
    @KafkaListener(
            id = "sourceAnalyticsTopicListener",
            topics = "${kafka-config.topic-name}",
            autoStartup = "false")
    public void receive(@Payload List<SourceAnalyticsAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of message received with keys {}, partitions {} and offsets {}, sending it to database: Thread id {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString(),
                Thread.currentThread().getId());
        List<AnalyticsEntity> twitterAnalyticsEntities = avroToDbEntityModelTransformer.getEntityModel(messages);
        analyticsRepository.batchPersist(twitterAnalyticsEntities);
        log.info("{} number of messaged send to database", twitterAnalyticsEntities.size());
    }

}
