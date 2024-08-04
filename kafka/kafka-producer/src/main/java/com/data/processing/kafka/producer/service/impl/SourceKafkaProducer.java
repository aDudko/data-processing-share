package com.data.processing.kafka.producer.service.impl;

import com.data.processing.kafka.avro.model.SourceAvroModel;
import com.data.processing.kafka.producer.service.KafkaProducer;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Slf4j
@Service
@AllArgsConstructor
public class SourceKafkaProducer implements KafkaProducer<Long, SourceAvroModel> {

    private KafkaTemplate<Long, SourceAvroModel> kafkaTemplate;


    @Override
    public void send(String topicName, Long key, SourceAvroModel message) {
        log.info("Sending message = '{}' to topic = '{}'", message, topicName);
        CompletableFuture<SendResult<Long, SourceAvroModel>> kafkaResultFuture = kafkaTemplate.send(topicName, key, message);
        kafkaResultFuture.whenComplete(getCallback(topicName, message));
    }

    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
            log.info("Closing kafka producer!");
            kafkaTemplate.destroy();
        }
    }

    private BiConsumer<SendResult<Long, SourceAvroModel>, Throwable> getCallback(String topicName, SourceAvroModel message) {
        return (result, ex) -> {
            if (ex == null) {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Received new metadata. Topic: {}; Partition {}; Offset {}; Timestamp {}, at time {}",
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp(),
                        System.nanoTime());
            } else {
                log.error("Error while sending message {} to topic {}", message.toString(), topicName, ex);
            }
        };
    }

}
