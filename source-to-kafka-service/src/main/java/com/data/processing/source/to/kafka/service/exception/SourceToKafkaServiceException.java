package com.data.processing.source.to.kafka.service.exception;

public class SourceToKafkaServiceException extends RuntimeException {

    public SourceToKafkaServiceException(String message) {
        super(message);
    }

    public SourceToKafkaServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
