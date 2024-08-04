package com.data.processing.kafka.streams.service.init.impl;

import com.data.processing.config.KafkaConfigData;
import com.data.processing.kafka.admin.client.KafkaAdminClient;
import com.data.processing.kafka.streams.service.init.StreamsInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaStreamsInitializer implements StreamsInitializer {

    private final KafkaConfigData kafkaConfigData;
    private final KafkaAdminClient kafkaAdminClient;


    @Override
    public void init() {
        kafkaAdminClient.checkTopicsCreated();
        kafkaAdminClient.checkSchemaRegistry();
        log.info("Topics with name {} is ready for operations!", kafkaConfigData.getTopicNamesToCreate().toArray());
    }

}
