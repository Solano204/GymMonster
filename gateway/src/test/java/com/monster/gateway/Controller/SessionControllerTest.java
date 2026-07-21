package com.monster.gateway.Controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.monster.gateway.Entities.Token;
import com.monster.gateway.InterfaceServices.Session;

import reactor.core.publisher.Mono;

/**
 * Slice test for SessionController in isolation from gateway's SecurityConfig (which is
 * covered separately by SecurityConfigIntegrationTest and depends on beans - JwtDecoder,
 * Redis, WebClient - this slice deliberately doesn't wire up).
 */
@WebFluxTest(controllers = SessionController.class, excludeAutoConfiguration = {
        ReactiveSecurityAutoConfiguration.class,
        ReactiveOAuth2ResourceServerAutoConfiguration.class
})
class SessionControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private Session impSession;

    private final Token token = new Token("carlos", "access-tok", "refresh-tok",
            Instant.now().plusSeconds(3600), Instant.now().plusSeconds(7200));

    @BeforeEach
    void setUp() {
        // no-op: kept for readability/parity with the rest of the suite's structure
    }

    @Test
    void login_delegatesToSessionService_withHeaderCredentials() {
        when(impSession.login("carlos", "s3cret")).thenReturn(Mono.just(token));

        webTestClient.post().uri("/GymMonster/auth/login")
                .header("username", "carlos")
                .header("password", "s3cret")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo("carlos")
                .jsonPath("$.accessToken").isEqualTo("access-tok")
                .jsonPath("$.refreshToken").isEqualTo("refresh-tok");

        verify(impSession).login("carlos", "s3cret");
    }

    @Test
    void login_returns400_whenUsernameHeaderIsMissing() {
        webTestClient.post().uri("/GymMonster/auth/login")
                .header("password", "s3cret")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void login_returns400_whenPasswordHeaderIsMissing() {
        webTestClient.post().uri("/GymMonster/auth/login")
                .header("username", "carlos")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void login_surfacesServiceErrorsAsAServerError() {
        when(impSession.login(eq("carlos"), eq("wrong"))).thenReturn(Mono.error(new RuntimeException("Keycloak down")));

        webTestClient.post().uri("/GymMonster/auth/login")
                .header("username", "carlos")
                .header("password", "wrong")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void logout_delegatesToSessionService_withRefreshTokenAndUsernameHeaders() {
        when(impSession.logout("refresh-tok", "carlos")).thenReturn(Mono.just("Logout Successful"));

        webTestClient.post().uri("/GymMonster/auth/logout")
                .header("refresh_token", "refresh-tok")
                .header("username", "carlos")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Logout Successful");

        verify(impSession).logout("refresh-tok", "carlos");
    }

    @Test
    void logout_returns400_whenRefreshTokenHeaderIsMissing() {
        webTestClient.post().uri("/GymMonster/auth/logout")
                .header("username", "carlos")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void logout_returns400_whenUsernameHeaderIsMissing() {
        webTestClient.post().uri("/GymMonster/auth/logout")
                .header("refresh_token", "refresh-tok")
                .exchange()
                .expectStatus().isBadRequest();
    }
}
