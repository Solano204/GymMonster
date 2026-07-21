package infraestrucutre.Adapters.Drivens.Configurations;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

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
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

/**
 * server-administrator is back-office-only: SecurityConfig permits only /actuator/**
 * and requires authentication for literally everything else. This is exactly the shape
 * of config that shipped broken in every other service in this portfolio (an accidental
 * "/**".permitAll() making .anyExchange().authenticated() dead code, per TESTING_NOTES.md) -
 * these assertions exist specifically to catch that regression here.
 */
// classes= is required here: this test's package (infraestrucutre.*) isn't nested under the
// app's own package (com.monster.server_administrator), so @SpringBootTest's default
// upward-package-scan for a @SpringBootConfiguration can never find ServerAdministratorApplication.
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = com.monster.server_administrator.ServerAdministratorApplication.class)
@AutoConfigureWebTestClient(timeout = "20000")
class SecurityConfigIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    private int port;

    @MockBean
    private ReactiveJwtDecoder jwtDecoder;

    @BeforeEach
    void bindToRealServer() {
        // The @Autowired WebTestClient (even under RANDOM_PORT) doesn't reliably bind to this
        // app's real Netty socket, which breaks Spring Security's exception translation for the
        // invalid-JWT path (JwtException surfaces as a raw 500 instead of being converted to
        // 401). Binding explicitly to the real @LocalServerPort avoids that.
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .responseTimeout(java.time.Duration.ofMillis(20000))
                .build();
    }

    @Test
    void actuatorHealth_isPubliclyAccessible_forKubernetesProbes() {
        webTestClient.get().uri("/actuator/health")
                .exchange()
                .expectStatus().value(status -> assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    void clientEndpoints_require401_withoutAToken() {
        webTestClient.get().uri("/api/admin/clients")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void actuatorAuditEvents_require401_withoutAToken() {
        // Only /actuator/** matches "/actuator/**" permitAll, so this passes the security
        // filter itself - what's asserted here is that a NON-actuator, non-explicitly-listed
        // path (below) is the one that must 401, distinguishing "everything" from "actuator only".
        webTestClient.get().uri("/api/admin/promotions")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void validJwt_passesTheSecurityLayer() {
        Jwt jwt = Jwt.withTokenValue("token-value")
                .header("alg", "none")
                .claim("sub", "carlos")
                .claim("preferred_username", "carlos")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        org.mockito.Mockito.when(jwtDecoder.decode(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(Mono.just(jwt));

        webTestClient.get().uri("/api/admin/clients")
                .header(HttpHeaders.AUTHORIZATION, "Bearer some-valid-looking-jwt")
                .exchange()
                .expectStatus().value(status -> assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    void invalidJwt_isRejectedWith401() {
        // BadJwtException specifically, not the generic base JwtException: real decoders throw
        // BadJwtException for a malformed/invalid token, and JwtReactiveAuthenticationManager
        // only maps THAT to a 401 (InvalidBearerTokenException) - a plain JwtException falls
        // through to AuthenticationServiceException, which is treated as an infra failure (500).
        org.mockito.Mockito.when(jwtDecoder.decode(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(Mono.error(new BadJwtException("bad token")));

        webTestClient.get().uri("/api/admin/clients")
                .header(HttpHeaders.AUTHORIZATION, "Bearer garbage")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
