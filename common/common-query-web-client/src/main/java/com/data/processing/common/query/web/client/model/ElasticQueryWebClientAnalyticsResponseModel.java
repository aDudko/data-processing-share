package com.data.processing.common.query.web.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticQueryWebClientAnalyticsResponseModel {

    private List<ElasticQueryWebClientResponseModel> queryResponseModels;

    private Long wordCount;

    private String fallbackMessage;

}
