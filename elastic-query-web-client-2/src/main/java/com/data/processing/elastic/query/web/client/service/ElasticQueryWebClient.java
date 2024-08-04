package com.data.processing.elastic.query.web.client.service;

import com.data.processing.common.query.web.client.model.ElasticQueryWebClientRequestModel;
import com.data.processing.common.query.web.client.model.ElasticQueryWebClientResponseModel;

import java.util.List;

public interface ElasticQueryWebClient {

    List<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel);

}
