package com.monster.gateway.Config;


import io.micrometer.observation.ObservationRegistry;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import com.monster.gateway.PropertiesUrl.ServicesUrl;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;


@Configuration
@Data
@AllArgsConstructor
public class WebClientConfig {

    private final ServicesUrl servicesUrl;

    // Doc 9: had no timeout at all - see server-administrator's
    // WebClientsConfig for the same fix and reasoning. Matters most here:
    // this is the edge gateway, so a hung downstream call would hang every
    // client request routed through it, not just one service's own work.
    @LoadBalanced
    @Bean
    public WebClient.Builder webClientBuilder(ObservationRegistry observationRegistry) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .responseTimeout(java.time.Duration.ofSeconds(5))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(5, TimeUnit.SECONDS)));
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    // RedisTokenValidationFilter/SecurityConfig/ImpSession all inject a plain
    // WebClient, not the Builder - build one from the load-balanced builder
    // above so it goes through the same timeout config and Eureka-aware
    // routing as everything else that calls other services by name.
    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }

    // Removed keycloakWebClient(): confirmed unused anywhere in gateway.
    // Also combined @LoadBalanced with a fixed baseUrl (Keycloak isn't a
    // load-balanced Eureka-registered service), which doesn't make sense
    // together - the same dead-and-wrong pattern found in web-page's
    // identical bean.
}
