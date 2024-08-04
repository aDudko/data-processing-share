package com.data.processing.reactive.elastic.query.service.service;

import com.data.processing.elastic.model.index.IndexModel;
import com.data.processing.elastic.model.index.impl.SourceIndexModel;
import reactor.core.publisher.Flux;

public interface ReactiveElasticQueryClient<T extends IndexModel> {

    Flux<SourceIndexModel> getIndexModelByText(String text);

}
