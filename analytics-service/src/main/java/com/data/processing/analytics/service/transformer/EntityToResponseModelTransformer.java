package com.data.processing.analytics.service.transformer;

import com.data.processing.analytics.service.dataaccess.entity.AnalyticsEntity;
import com.data.processing.analytics.service.model.AnalyticsResponseModel;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EntityToResponseModelTransformer {

    public Optional<AnalyticsResponseModel> getResponseModel(AnalyticsEntity analyticsEntity) {
        if (analyticsEntity == null)
            return Optional.empty();
        return Optional.ofNullable(AnalyticsResponseModel
                .builder()
                .id(analyticsEntity.getId())
                .word(analyticsEntity.getWord())
                .wordCount(analyticsEntity.getWordCount())
                .build());
    }

}
