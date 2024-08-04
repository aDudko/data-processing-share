package com.data.processing.elastic.index.client.service;

import com.data.processing.elastic.model.index.IndexModel;

import java.util.List;

public interface ElasticIndexClient<T extends IndexModel> {

    List<String> save(List<T> documents);

}
