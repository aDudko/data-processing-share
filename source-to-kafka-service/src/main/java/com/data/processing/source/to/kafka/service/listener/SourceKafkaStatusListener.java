package com.data.processing.source.to.kafka.service.listener;

import com.data.processing.config.KafkaConfigData;
import com.data.processing.kafka.avro.model.SourceAvroModel;
import com.data.processing.kafka.producer.service.KafkaProducer;
import com.data.processing.source.to.kafka.service.transformer.SourceStatusToAvroTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;

@Slf4j
@Component
@RequiredArgsConstructor
public class SourceKafkaStatusListener extends StatusAdapter {

    private final KafkaConfigData kafkaConfigData;
    private final KafkaProducer<Long, SourceAvroModel> kafkaProducer;
    private final SourceStatusToAvroTransformer sourceStatusToAvroMapper;


    @Override
    public void onStatus(Status status) {
        log.info("Received status text '{}' sending to kafka topic: {}", status.getText(), kafkaConfigData.getTopicName());
        SourceAvroModel sourceAvroModel = sourceStatusToAvroMapper.getSourceAvroModelFromStatus(status);
        kafkaProducer.send(
                kafkaConfigData.getTopicName(),
                sourceAvroModel.getUserId(),
                sourceAvroModel);
    }

}
