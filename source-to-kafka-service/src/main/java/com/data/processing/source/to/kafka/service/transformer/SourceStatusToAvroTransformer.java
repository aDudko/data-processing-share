package com.data.processing.source.to.kafka.service.transformer;

import com.data.processing.kafka.avro.model.SourceAvroModel;
import org.springframework.stereotype.Component;
import twitter4j.Status;

@Component
public class SourceStatusToAvroTransformer {

    public SourceAvroModel getSourceAvroModelFromStatus(Status status) {
        return SourceAvroModel.newBuilder()
                .setId(status.getId())
                .setText(status.getText())
                .setUserId(status.getUser().getId())
                .setCreatedAt(status.getCreatedAt().getTime())
                .build();
    }

}
