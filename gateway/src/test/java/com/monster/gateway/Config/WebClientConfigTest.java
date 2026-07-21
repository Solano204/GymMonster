package com.monster.gateway.Config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import com.monster.gateway.PropertiesUrl.ServicesUrl;

import io.micrometer.observation.ObservationRegistry;

class WebClientConfigTest {

    @Test
    void webClientBuilder_buildsAUsableWebClient() {
        WebClientConfig config = new WebClientConfig(new ServicesUrl());

        WebClient.Builder builder = config.webClientBuilder(ObservationRegistry.NOOP);

        assertThat(builder).isNotNull();
        assertThat(builder.build()).isNotNull();
    }

    @Test
    void webClientBuilder_returnsANewBuilderInstanceOnEachCall() {
        WebClientConfig config = new WebClientConfig(new ServicesUrl());

        WebClient.Builder first = config.webClientBuilder(ObservationRegistry.NOOP);
        WebClient.Builder second = config.webClientBuilder(ObservationRegistry.NOOP);

        // Guards against a future refactor accidentally caching/sharing a single
        // builder (and therefore a single HttpClient/timeout config) across every
        // downstream call site that autowires WebClient.Builder.
        assertThat(first).isNotSameAs(second);
    }

    @Test
    void getServicesUrl_returnsTheConfiguredInstance() {
        ServicesUrl servicesUrl = new ServicesUrl();
        WebClientConfig config = new WebClientConfig(servicesUrl);

        assertThat(config.getServicesUrl()).isSameAs(servicesUrl);
    }
}
