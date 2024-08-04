package com.data.processing.source.to.kafka.service;

import com.data.processing.source.to.kafka.service.init.StreamInitializer;
import com.data.processing.source.to.kafka.service.runner.StreamRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
@ComponentScan(basePackages = "com.data.processing")
public class SourceToKafkaServiceApplication implements CommandLineRunner {

    private final StreamRunner streamRunner;
    private final StreamInitializer streamInitializer;


    public static void main(String[] args) {
        SpringApplication.run(SourceToKafkaServiceApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        log.info("App starts...");
        streamInitializer.init();
        streamRunner.start();
    }

}
