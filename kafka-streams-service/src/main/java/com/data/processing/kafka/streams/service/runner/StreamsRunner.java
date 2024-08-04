package com.data.processing.kafka.streams.service.runner;

public interface StreamsRunner<K, V> {

    void start();

    default V getValueByKey(K key) {
        return null;
    }

}
