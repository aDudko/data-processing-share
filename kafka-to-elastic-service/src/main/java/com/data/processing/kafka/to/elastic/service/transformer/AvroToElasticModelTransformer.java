package com.data.processing.kafka.to.elastic.service.transformer;

import com.data.processing.elastic.model.index.impl.SourceIndexModel;
import com.data.processing.kafka.avro.model.SourceAvroModel;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AvroToElasticModelTransformer {

    public List<SourceIndexModel> getElasticModels(List<SourceAvroModel> avroModels) {
        return avroModels.stream()
                .map(avroModel -> SourceIndexModel.builder()
                        .userId(avroModel.getUserId())
                        .id(String.valueOf(avroModel.getId()))
                        .text(avroModel.getText())
                        .createdAt(ZonedDateTime.ofInstant(Instant.ofEpochMilli(avroModel.getCreatedAt()),
                                ZoneId.systemDefault()))
                        .build())
                .collect(Collectors.toList());
    }

}
