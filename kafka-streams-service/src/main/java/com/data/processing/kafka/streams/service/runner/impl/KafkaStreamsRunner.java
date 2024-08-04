package com.data.processing.kafka.streams.service.runner.impl;

import com.data.processing.config.KafkaConfigData;
import com.data.processing.config.KafkaStreamsConfigData;
import com.data.processing.kafka.avro.model.SourceAnalyticsAvroModel;
import com.data.processing.kafka.avro.model.SourceAvroModel;
import com.data.processing.kafka.streams.service.runner.StreamsRunner;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

@Slf4j
@Component
public class KafkaStreamsRunner implements StreamsRunner<String, Long> {


    private static final String REGEX = "\\W+";

    private final KafkaStreamsConfigData kafkaStreamsConfigData;
    private final KafkaConfigData kafkaConfigData;
    private final Properties streamsConfiguration;
    private KafkaStreams kafkaStreams;

    private volatile ReadOnlyKeyValueStore<String, Long> keyValueStore;

    public KafkaStreamsRunner(
            KafkaStreamsConfigData kafkaStreamsConfig,
            KafkaConfigData kafkaConfig,
            @Qualifier("streamConfiguration") Properties streamsConfiguration) {
        this.kafkaStreamsConfigData = kafkaStreamsConfig;
        this.kafkaConfigData = kafkaConfig;
        this.streamsConfiguration = streamsConfiguration;
    }

    @Override
    public void start() {
        final Map<String, String> serdeConfig = Collections.singletonMap(
                kafkaConfigData.getSchemaRegistryUrlKey(),
                kafkaConfigData.getSchemaRegistryUrl());
        final StreamsBuilder streamsBuilder = new StreamsBuilder();
        final KStream<Long, SourceAvroModel> sourceAvroModelKStream = getSourceAvroModelKStream(serdeConfig, streamsBuilder);
        createTopology(sourceAvroModelKStream, serdeConfig);
        startStreaming(streamsBuilder);
    }

    @Override
    public Long getValueByKey(String word) {
        if (kafkaStreams != null && kafkaStreams.state() == KafkaStreams.State.RUNNING) {
            if (keyValueStore == null) {
                synchronized (this) {
                    if (keyValueStore == null) {
                        keyValueStore = kafkaStreams.store(StoreQueryParameters.fromNameAndType(
                                kafkaStreamsConfigData.getWordCountStoreName(),
                                QueryableStoreTypes.keyValueStore()));
                    }
                }
            }
            return keyValueStore.get(word.toLowerCase());
        }
        return 0L;
    }

    @PreDestroy
    public void close() {
        if (kafkaStreams != null) {
            kafkaStreams.close();
            log.info("Kafka streaming closed!");
        }
    }


    private void startStreaming(StreamsBuilder streamsBuilder) {
        final Topology topology = streamsBuilder.build();
        log.info("Defined topology: {}", topology.describe());
        kafkaStreams = new KafkaStreams(topology, streamsConfiguration);
        kafkaStreams.start();
        log.info("Kafka streaming started...");
    }

    private void createTopology(
            KStream<Long, SourceAvroModel> sourceAvroModelKStream,
            Map<String, String> serdeConfig) {
        Pattern pattern = Pattern.compile(REGEX, Pattern.UNICODE_CHARACTER_CLASS);
        Serde<SourceAnalyticsAvroModel> serdeSourceAnalyticsAvroModel = getSerdeAnalyticsModel(serdeConfig);
        sourceAvroModelKStream
                .flatMapValues(value -> Arrays.asList(pattern.split(value.getText().toLowerCase())))
                .groupBy((key, word) -> word)
                .count(Materialized.as(kafkaStreamsConfigData.getWordCountStoreName()))
                .toStream()
                .map(mapToAnalyticsModel())
                .to(kafkaStreamsConfigData.getOutputTopicName(), Produced.with(Serdes.String(), serdeSourceAnalyticsAvroModel));

    }

    private KeyValueMapper<String, Long, KeyValue<? extends String, ? extends SourceAnalyticsAvroModel>>
    mapToAnalyticsModel() {
        return (word, count) -> {
            log.info("Sending to topic: {}, word: {} - count: {}",
                    kafkaStreamsConfigData.getOutputTopicName(), word, count);
            return new KeyValue<>(word, SourceAnalyticsAvroModel
                    .newBuilder()
                    .setWord(word)
                    .setWordCount(count)
                    .setCreatedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                    .build());
        };
    }

    private Serde<SourceAnalyticsAvroModel> getSerdeAnalyticsModel(Map<String, String> serdeConfig) {
        Serde<SourceAnalyticsAvroModel> serdeSourceAnalyticsAvroModel = new SpecificAvroSerde<>();
        serdeSourceAnalyticsAvroModel.configure(serdeConfig, false);
        return serdeSourceAnalyticsAvroModel;
    }

    private KStream<Long, SourceAvroModel> getSourceAvroModelKStream(
            Map<String, String> serdeConfig,
            StreamsBuilder streamsBuilder) {
        final Serde<SourceAvroModel> serdeSourceAvroModel = new SpecificAvroSerde<>();
        serdeSourceAvroModel.configure(serdeConfig, false);
        return streamsBuilder.stream(
                kafkaStreamsConfigData.getInputTopicName(),
                Consumed.with(Serdes.Long(),
                serdeSourceAvroModel));
    }

}
