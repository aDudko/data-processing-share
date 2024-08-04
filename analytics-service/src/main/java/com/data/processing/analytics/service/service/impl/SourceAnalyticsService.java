package com.data.processing.analytics.service.service.impl;

import com.data.processing.analytics.service.dataaccess.repository.AnalyticsRepository;
import com.data.processing.analytics.service.model.AnalyticsResponseModel;
import com.data.processing.analytics.service.service.AnalyticsService;
import com.data.processing.analytics.service.transformer.EntityToResponseModelTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SourceAnalyticsService implements AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final EntityToResponseModelTransformer entityToResponseModelTransformer;


    @Override
    public Optional<AnalyticsResponseModel> getWordAnalytics(String word) {
        return entityToResponseModelTransformer.getResponseModel(
                analyticsRepository.getAnalyticsEntitiesByWord(word, PageRequest.of(0, 1))
                        .stream().findFirst().orElse(null));
    }

}
