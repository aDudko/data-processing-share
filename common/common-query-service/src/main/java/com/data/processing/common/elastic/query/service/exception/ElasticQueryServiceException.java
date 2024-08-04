package com.data.processing.common.elastic.query.service.exception;

public class ElasticQueryServiceException extends RuntimeException {

    public ElasticQueryServiceException(String message) {
        super(message);
    }

    public ElasticQueryServiceException(String message, Throwable t) {
        super(message, t);
    }

}
