package com.data.processing.elastic.query.web.client.service;

import com.data.processing.common.query.web.client.model.ElasticQueryWebClientAnalyticsResponseModel;
import com.data.processing.common.query.web.client.model.ElasticQueryWebClientRequestModel;

public interface ElasticQueryWebClient {

    ElasticQueryWebClientAnalyticsResponseModel getDataByText(ElasticQueryWebClientRequestModel requestModel);

}
