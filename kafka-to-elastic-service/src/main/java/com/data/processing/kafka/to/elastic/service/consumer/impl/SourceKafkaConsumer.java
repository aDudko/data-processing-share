package com.data.processing.kafka.to.elastic.service.consumer.impl;

import com.data.processing.config.KafkaConfigData;
import com.data.processing.config.KafkaConsumerConfigData;
import com.data.processing.elastic.index.client.service.ElasticIndexClient;
import com.data.processing.elastic.model.index.impl.SourceIndexModel;
import com.data.processing.kafka.admin.client.KafkaAdminClient;
import com.data.processing.kafka.avro.model.SourceAvroModel;
import com.data.processing.kafka.to.elastic.service.consumer.KafkaConsumer;
import com.data.processing.kafka.to.elastic.service.transformer.AvroToElasticModelTransformer;
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
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SourceKafkaConsumer implements KafkaConsumer<SourceAvroModel> {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final KafkaAdminClient kafkaAdminClient;
    private final KafkaConfigData kafkaConfigData;
    private final KafkaConsumerConfigData kafkaConsumerConfigData;
    private final AvroToElasticModelTransformer avroToElasticModelTransformer;
    private final ElasticIndexClient<SourceIndexModel> elasticIndexClient;


    @EventListener
    public void onAppStarted(ApplicationStartedEvent event) {
        kafkaAdminClient.checkTopicsCreated();
        log.info("Topics with name: {} is ready for operations!", kafkaConfigData.getTopicNamesToCreate().toArray());
        Objects.requireNonNull(kafkaListenerEndpointRegistry
                .getListenerContainer(kafkaConsumerConfigData.getConsumerGroupId())).start();
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.consumer-group-id}", topics = "${kafka-config.topic-name}")
    public void receive(@Payload List<SourceAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<Long> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of message received with keys: {}, partitions: {} and offsets: {}, " +
                        "sending it to elastic: Thread id: {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString(),
                Thread.currentThread().getId());
        List<SourceIndexModel> sourceIndexModels = avroToElasticModelTransformer.getElasticModels(messages);
        List<String> documentIds = elasticIndexClient.save(sourceIndexModels);
        log.info("Documents saved to elasticsearch with ids: {}", documentIds.toArray());
    }

}
