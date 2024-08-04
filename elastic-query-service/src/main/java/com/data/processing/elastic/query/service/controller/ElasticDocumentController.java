package com.data.processing.elastic.query.service.controller;

import com.data.processing.common.elastic.query.service.model.ElasticQueryServiceRequestModel;
import com.data.processing.common.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.data.processing.elastic.query.service.model.ElasticQueryServiceAnalyticsResponseModel;
import com.data.processing.elastic.query.service.model.ElasticQueryServiceResponseModelV2;
import com.data.processing.elastic.query.service.security.SourceQueryUser;
import com.data.processing.elastic.query.service.service.ElasticQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Slf4j
@PreAuthorize("isAuthenticated()")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/documents", produces = "application/vnd.api.v1+json")
public class ElasticDocumentController {

    private final ElasticQueryService elasticQueryService;

    @Value("${server.port}")
    private String port;


    @Operation(summary = "Get all elastic documents")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                             schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    @GetMapping("")
    public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments() {
        List<ElasticQueryServiceResponseModel> response = elasticQueryService.getAllDocuments();
        log.info("Elasticsearch returned {} of documents", response.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get elastic document by text")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('APP_USER_ROLE') || hasRole('APP_SUPER_USER_ROLE') || hasAuthority('SCOPE_APP_USER_ROLE')")
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    @PostMapping("/get-document-by-text")
    public @ResponseBody ResponseEntity<ElasticQueryServiceAnalyticsResponseModel> getDocumentByText(
            @RequestBody @Valid ElasticQueryServiceRequestModel elasticQueryServiceRequestModel,
            @AuthenticationPrincipal SourceQueryUser principal,
            @RegisteredOAuth2AuthorizedClient("keycloak")
            OAuth2AuthorizedClient oAuth2AuthorizedClient) {
        log.info("User {} querying documents for text {}", principal.getUsername(),
                elasticQueryServiceRequestModel.getText());
        ElasticQueryServiceAnalyticsResponseModel response =
                elasticQueryService.getDocumentByText(elasticQueryServiceRequestModel.getText(),
                        oAuth2AuthorizedClient.getAccessToken().getTokenValue());
        log.info("Elasticsearch returned {} of documents on port {}",
                response.getQueryResponseModels().size(), port);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get elastic document by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasPermission(#id, 'ElasticQueryServiceResponseModel', 'READ')")
    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<ElasticQueryServiceResponseModel> getDocumentByIdV1(
            @PathVariable @NotEmpty String id) {
        ElasticQueryServiceResponseModel elasticQueryServiceResponseModel = elasticQueryService.getDocumentById(id);
        log.debug("Elasticsearch returned document with id: {} on port: {}", id, port);
        return ResponseEntity.ok(elasticQueryServiceResponseModel);
    }

    @Operation(summary = "Get elastic document by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response", content = {
                    @Content(mediaType = "application/vnd.api.v2+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModelV2.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/{id}", produces = "application/vnd.api.v2+json")
    public @ResponseBody ResponseEntity<ElasticQueryServiceResponseModelV2> getDocumentByIdV2(
            @PathVariable @NotEmpty String id) {
        ElasticQueryServiceResponseModel elasticQueryServiceResponseModel = elasticQueryService.getDocumentById(id);
        ElasticQueryServiceResponseModelV2 responseModelV2 = getV2Model(elasticQueryServiceResponseModel);
        log.debug("Elasticsearch returned document with id: {} on port: {}", id, port);
        return ResponseEntity.ok(responseModelV2);
    }


    // Example of versioning API
    private ElasticQueryServiceResponseModelV2 getV2Model(ElasticQueryServiceResponseModel responseModel) {
        ElasticQueryServiceResponseModelV2 responseModelV2 = ElasticQueryServiceResponseModelV2.builder()
                .id(Long.parseLong(responseModel.getId()))
                .userId(responseModel.getUserId())
                .text(responseModel.getText())
                .text2("Version 2 text")
                .build();
        responseModelV2.add(responseModel.getLinks());
        return responseModelV2;
    }

}
