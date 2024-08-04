package com.data.processing.elastic.query.service.model;

import com.data.processing.common.elastic.query.service.model.ElasticQueryServiceResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticQueryServiceAnalyticsResponseModel {

    private List<ElasticQueryServiceResponseModel> queryResponseModels;

    private Long wordCount;

}
