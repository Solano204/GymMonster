package com.monster.gateway.Config;


import io.micrometer.observation.ObservationRegistry;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {


    @Bean
    public WebClient.Builder webClientBuilder(ObservationRegistry observationRegistry) {
        return WebClient.builder();
    }

    @Bean
    public WebClient keycloakWebClient() {
        return WebClient.builder()
                    .baseUrl("http://localhost:8181") // Direct URL to Keycloak
                .build();
    }
}
