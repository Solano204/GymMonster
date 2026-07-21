package com.monster.gateway.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import com.monster.gateway.PropertiesUrl.ServicesUrl;

/**
 * coder wires the ReactiveJwtDecoder bean gateway's SecurityConfig authenticates
 * every non-public request with, so a bad JWK-set URI here silently breaks auth
 * for the whole edge.
 */
class CoderTest {

    private ServicesUrl servicesUrlWithKeycloakUrl(String url) {
        ServicesUrl servicesUrl = new ServicesUrl();
        ServicesUrl.Keycloak keycloak = new ServicesUrl.Keycloak();
        keycloak.setUrl(url);
        keycloak.setClientId("Docker-Gym");
        keycloak.setClientSecret("test-secret");
        servicesUrl.setKeycloak(keycloak);
        return servicesUrl;
    }

    @Test
    void jwtDecoderBean_isCreated_whenKeycloakUrlIsConfigured() {
        coder coderConfig = new coder(servicesUrlWithKeycloakUrl("http://keycloak:8181"));

        ReactiveJwtDecoder decoder = coderConfig.jwtDecoder();

        assertThat(decoder).isNotNull();
    }

    @Test
    void jwtDecoderBean_buildsSuccessfully_regardlessOfTrailingSlashInUrl() {
        coder coderConfig = new coder(servicesUrlWithKeycloakUrl("http://keycloak:8181/"));

        assertThat(coderConfig.jwtDecoder()).isNotNull();
    }

    @Test
    void jwtDecoderBean_throwsNullPointerException_whenKeycloakBlockIsMissing() {
        ServicesUrl servicesUrl = new ServicesUrl();
        // Keycloak sub-object never set - the same misconfiguration shape as an
        // incomplete svc.keycloak.* property block reaching this bean.
        coder coderConfig = new coder(servicesUrl);

        assertThatThrownBy(coderConfig::jwtDecoder).isInstanceOf(NullPointerException.class);
    }

    @Test
    void getServicesUrl_returnsTheConfiguredInstance() {
        ServicesUrl servicesUrl = servicesUrlWithKeycloakUrl("http://keycloak:8181");
        coder coderConfig = new coder(servicesUrl);

        assertThat(coderConfig.getServicesUrl()).isSameAs(servicesUrl);
    }
}
