package com.data.processing.elastic.query.service.model.assembler;

import com.data.processing.common.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.data.processing.elastic.model.index.impl.SourceIndexModel;
import com.data.processing.elastic.query.service.controller.ElasticDocumentController;
import com.data.processing.common.elastic.query.service.transformer.ElasticToResponseModelTransformer;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ElasticQueryServiceResponseModelAssembler
        extends RepresentationModelAssemblerSupport<SourceIndexModel, ElasticQueryServiceResponseModel> {

    private final ElasticToResponseModelTransformer elasticToResponseModelTransformer;

    public ElasticQueryServiceResponseModelAssembler(ElasticToResponseModelTransformer transformer) {
        super(ElasticDocumentController.class, ElasticQueryServiceResponseModel.class);
        this.elasticToResponseModelTransformer = transformer;
    }


    @Override
    public ElasticQueryServiceResponseModel toModel(SourceIndexModel sourceIndexModel) {
        ElasticQueryServiceResponseModel responseModel =
                elasticToResponseModelTransformer.getResponseModel(sourceIndexModel);
        responseModel.add(linkTo(
                methodOn(ElasticDocumentController.class)
                        .getDocumentByIdV1((sourceIndexModel.getId()))).withSelfRel());
        responseModel.add(linkTo(
                ElasticDocumentController.class).withRel("documents"));
        return responseModel;
    }


    public List<ElasticQueryServiceResponseModel> toModels(List<SourceIndexModel> sourceIndexModels) {
        return sourceIndexModels.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

}

