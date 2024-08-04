package com.data.processing.analytics.service.service;

import com.data.processing.analytics.service.model.AnalyticsResponseModel;

import java.util.Optional;

public interface AnalyticsService {

    Optional<AnalyticsResponseModel> getWordAnalytics(String word);

}
