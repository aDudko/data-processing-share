package com.data.processing.common.elastic.query.service.transformer;

import com.data.processing.common.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.data.processing.elastic.model.index.impl.SourceIndexModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ElasticToResponseModelTransformer {

    public ElasticQueryServiceResponseModel getResponseModel(SourceIndexModel sourceIndexModel) {
        return ElasticQueryServiceResponseModel
                .builder()
                .id(sourceIndexModel.getId())
                .userId(sourceIndexModel.getUserId())
                .text(sourceIndexModel.getText())
                .createdAt(sourceIndexModel.getCreatedAt())
                .build();
    }

    public List<ElasticQueryServiceResponseModel> getResponseModels(List<SourceIndexModel> sourceIndexModels) {
        return sourceIndexModels.stream().map(this::getResponseModel).collect(Collectors.toList());
    }

}
