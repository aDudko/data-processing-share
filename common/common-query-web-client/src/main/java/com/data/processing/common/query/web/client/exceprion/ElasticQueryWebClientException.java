package com.data.processing.common.query.web.client.exceprion;

public class ElasticQueryWebClientException extends RuntimeException {

    public ElasticQueryWebClientException(String message) {
        super(message);
    }

    public ElasticQueryWebClientException(String message, Throwable t) {
        super(message, t);
    }

}
