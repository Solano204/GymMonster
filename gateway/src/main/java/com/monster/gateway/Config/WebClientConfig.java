package com.monster.gateway.Config;


import io.micrometer.observation.ObservationRegistry;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.monster.gateway.PropertiesUrl.ServicesUrl;


@Configuration
@Data
@AllArgsConstructor
public class WebClientConfig {

    private final ServicesUrl servicesUrl;

    @LoadBalanced
    @Bean
    public WebClient.Builder webClientBuilder(ObservationRegistry observationRegistry) {
        return WebClient.builder();
    }


    @LoadBalanced
    @Bean
    public WebClient keycloakWebClient() {
        return WebClient.builder()
                    .baseUrl(servicesUrl.getKeycloak().getUrl()) // Direct URL to Keycloak
                .build();
    }
}
