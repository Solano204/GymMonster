package com.monster.gateway.Config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.monster.gateway.FATHER;

import java.time.Instant;

import reactor.core.publisher.Mono;

/**
 * Locks down gateway's own SecurityConfig authorizeExchange rules end-to-end through a
 * real Spring context. The rest of the portfolio's SecurityConfigs (see TESTING_NOTES.md
 * across the other 4 services) have historically shipped with an accidental "/**".permitAll()
 * glob that made every .anyExchange().authenticated() rule dead code - these assertions
 * exist specifically to catch that class of regression here too.
 *
 * Assertions only check "was this blocked by security" (401 vs. not-401), never the eventual
 * response body/status from a downstream route, since /api/admin/** and /api/page/** proxy to
 * server-administrator/web-page which aren't running in this test's context.
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "20000")
class SecurityConfigIntegrationTest extends FATHER {

    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    private int port;

    @MockBean
    private ReactiveJwtDecoder jwtDecoder;

    @BeforeEach
    void bindToRealServer() {
        // The @Autowired WebTestClient (even under RANDOM_PORT) doesn't reliably bind to this
        // app's real Netty socket - its requests' getURI() comes back schemeless/hostless
        // (just the path, no "http://host:port" prefix), which crashes Spring's same-origin CORS
        // check (it asserts scheme/host/port are non-null) whenever an Origin header is present,
        // turning a valid CORS preflight into a spurious 403. Binding explicitly to the real
        // @LocalServerPort avoids that and matches this class's "real Spring context" intent.
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .responseTimeout(java.time.Duration.ofMillis(20000))
                .build();
    }

    @Test
    void authLogin_isPermittedWithoutAnyCredentials() {
        webTestClient.post().uri("/GymMonster/auth/login")
                .exchange()
                .expectStatus().value(status -> assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    void authLogout_isPermittedWithoutAnyCredentials() {
        webTestClient.post().uri("/GymMonster/auth/logout")
                .exchange()
                .expectStatus().value(status -> assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    void actuatorHealth_isPubliclyAccessible_forKubernetesProbes() {
        webTestClient.get().uri("/actuator/health")
                .exchange()
                .expectStatus().value(status -> assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    void publicPageBrowsingEndpoints_arePermitted_withoutAToken() {
        webTestClient.get().uri("/api/page/allMemberships")
                .exchange()
                .expectStatus().value(status -> assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    void registerClient_isPermitted_withoutAToken() {
        webTestClient.post().uri("/api/page/registerClient")
                .exchange()
                .expectStatus().value(status -> assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    void adminEndpoints_require401_withoutAToken() {
        webTestClient.get().uri("/api/admin/clients")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void actuatorAuditEvents_require401_withoutAToken() {
        // Only /actuator/health(/**) is permitAll; auditevents falls through to
        // .anyExchange().authenticated() - this is the exact "one uncovered seam"
        // shape flagged as a recurring bug class across the portfolio.
        webTestClient.get().uri("/actuator/auditevents")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void clientAccountEndpoints_require401_withoutAToken() {
        webTestClient.put().uri("/api/page/clients/jdoe/changePassword")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void validJwt_passesTheSecurityLayer_forAProtectedActuatorEndpoint() {
        Jwt jwt = Jwt.withTokenValue("token-value")
                .header("alg", "none")
                .claim("sub", "carlos")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        org.mockito.Mockito.when(jwtDecoder.decode(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(Mono.just(jwt));

        webTestClient.get().uri("/actuator/auditevents")
                .header(HttpHeaders.AUTHORIZATION, "Bearer some-valid-looking-jwt")
                .exchange()
                .expectStatus().value(status -> assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    void invalidJwt_isRejectedWith401() {
        // A real ReactiveJwtDecoder throws BadJwtException (not the bare JwtException
        // base class) for a malformed/invalid token - JwtReactiveAuthenticationManager
        // only maps that specific subtype to a 401 InvalidBearerTokenException; any
        // other JwtException is treated as an unexpected auth-infrastructure failure
        // and surfaces as a 500 instead.
        org.mockito.Mockito.when(jwtDecoder.decode(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(Mono.error(new org.springframework.security.oauth2.jwt.BadJwtException("bad token")));

        webTestClient.get().uri("/api/admin/clients")
                .header(HttpHeaders.AUTHORIZATION, "Bearer garbage")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void corsPreflight_allowsCrossOriginRequestsFromTheStandaloneFrontendPanel() {
        webTestClient.options().uri("/GymMonster/auth/login")
                .header(HttpHeaders.ORIGIN, "http://localhost:5500")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.POST.name())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueMatches(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ".*");
    }
}
