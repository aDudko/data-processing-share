package com.data.processing.kafka.streams.service.security;

import com.data.processing.config.KafkaStreamsServiceConfigData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Qualifier(value = "kafka-streams-service-audience-validator")
@Component
@RequiredArgsConstructor
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final KafkaStreamsServiceConfigData kafkaStreamsServiceConfig;


    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (jwt.getAudience().contains(kafkaStreamsServiceConfig.getCustomAudience())) {
            return OAuth2TokenValidatorResult.success();
        } else {
            OAuth2Error audienceError = new OAuth2Error("invalid_token",
                    "The required audience " +
                            kafkaStreamsServiceConfig.getCustomAudience() +
                            " is missing!", null);
            return OAuth2TokenValidatorResult.failure(audienceError);
        }
    }

}
