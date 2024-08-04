package com.data.processing.elastic.query.web.client.controller;

import com.data.processing.common.query.web.client.model.ElasticQueryWebClientAnalyticsResponseModel;
import com.data.processing.common.query.web.client.model.ElasticQueryWebClientRequestModel;
import com.data.processing.elastic.query.web.client.service.ElasticQueryWebClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;

@Slf4j
@Controller
public class QueryController {

    private final ElasticQueryWebClient elasticQueryWebClient;

    public QueryController(ElasticQueryWebClient webClient) {
        this.elasticQueryWebClient = webClient;
    }


    @GetMapping("")
    public String index() {
        return "index";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("elasticQueryWebClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());
        return "home";
    }

    @PostMapping("/query-by-text")
    public String queryByText(@Valid ElasticQueryWebClientRequestModel requestModel,
                              Model model) {
        log.info("Querying with text {}", requestModel.getText());
        ElasticQueryWebClientAnalyticsResponseModel responseModel = elasticQueryWebClient.getDataByText(requestModel);
        model.addAttribute("elasticQueryWebClientResponseModels",
                responseModel.getQueryResponseModels());
        model.addAttribute("wordCount", responseModel.getWordCount());
        model.addAttribute("fallbackMessage", responseModel.getFallbackMessage());
        model.addAttribute("searchText", requestModel.getText());
        model.addAttribute("elasticQueryWebClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());
        return "home";
    }

}
