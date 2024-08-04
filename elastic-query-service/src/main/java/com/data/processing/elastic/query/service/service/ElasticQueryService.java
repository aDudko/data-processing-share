package com.data.processing.elastic.query.service.service;

import com.data.processing.common.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.data.processing.elastic.query.service.model.ElasticQueryServiceAnalyticsResponseModel;

import java.util.List;

public interface ElasticQueryService {

    ElasticQueryServiceResponseModel getDocumentById(String id);

    ElasticQueryServiceAnalyticsResponseModel getDocumentByText(String text, String accessToken);

    List<ElasticQueryServiceResponseModel> getAllDocuments();

}
