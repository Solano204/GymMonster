package com.monster.gateway.PropertiesUrl;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.info.InfoProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "svc")
@Data
public class ServicesUrl {
    

    private Keycloak keycloak;
    private RedisProperties redis;

    @Data
    public static class Keycloak {
        private String clientId;
        private String clientSecret;
        private String url;
    }

    @Data
    public static class RedisProperties {
        private String host;
        private int port;
        private String password;
    }
}


