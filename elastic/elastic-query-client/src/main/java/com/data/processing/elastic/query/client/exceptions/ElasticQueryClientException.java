package com.data.processing.elastic.query.client.exceptions;

public class ElasticQueryClientException extends RuntimeException {

    public ElasticQueryClientException(String message) {
        super(message);
    }

    public ElasticQueryClientException(String message, Throwable t) {
        super(message, t);
    }

}