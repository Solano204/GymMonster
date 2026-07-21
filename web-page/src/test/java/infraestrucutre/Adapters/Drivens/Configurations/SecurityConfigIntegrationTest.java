package infraestrucutre.Adapters.Drivens.Configurations;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import reactor.core.publisher.Mono;

/**
 * Self-contained (no docker-compose needed) unlike this portfolio's other SecurityConfig
 * integration tests - Redisson's RedissonClient bean connects eagerly at context startup
 * (unlike a lazy Kafka producer factory), so a Testcontainers Redis + @DynamicPropertySource
 * override is required just to let the context start at all. Mirrors gateway's FATHER pattern.
 *
 * Unlike server-informations/server-register, web-page's SecurityConfig does NOT have the
 * broad "/**".permitAll() bug (it was already fixed here as part of this pilot project) - these
 * assertions confirm the fix holds, not document a regression.
 */
@Testcontainers
// classes= is required here: this test's package (infraestrucutre.*) isn't nested under the
// app's own package (com.monster.web_page), so @SpringBootTest's default upward-package-scan
// for a @SpringBootConfiguration can never find WebPageApplication.
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = com.monster.web_page.WebPageApplication.class)
@AutoConfigureWebTestClient(timeout = "20000")
class SecurityConfigIntegrationTest {

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:6.0.9"))
            .withExposedPorts(6379)
            .withCommand("redis-server", "--requirepass", "testing");

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("svc.redis.host", redisContainer::getHost);
        registry.add("svc.redis.port", () -> redisContainer.getMappedPort(6379));
        registry.add("svc.redis.password", () -> "testing");
    }

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ReactiveJwtDecoder jwtDecoder;

    @Test
    void actuatorHealth_isPubliclyAccessible() {
        webTestClient.get().uri("/actuator/health")
                .exchange()
                .expectStatus().value(status -> assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    void publicBrowsingGetEndpoints_arePermitted_withoutAToken() {
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
    void clientProfileEndpoints_require401_withoutAToken() {
        webTestClient.get().uri("/api/page/clients/jdoe/allInformation")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void nonGetRequestsToPublicPaths_stillRequireAuthentication() {
        // Only GET is permitAll on the public browsing paths - confirms a PUT/DELETE against
        // the same path prefix doesn't accidentally slip through too.
        webTestClient.put().uri("/api/page/allMemberships")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void validJwt_passesTheSecurityLayer_forAProtectedClientEndpoint() {
        Jwt jwt = Jwt.withTokenValue("token-value")
                .header("alg", "none")
                .claim("sub", "jdoe")
                .claim("preferred_username", "jdoe")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        org.mockito.Mockito.when(jwtDecoder.decode(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(Mono.just(jwt));

        webTestClient.get().uri("/api/page/clients/jdoe/allInformation")
                .header(HttpHeaders.AUTHORIZATION, "Bearer some-valid-looking-jwt")
                .exchange()
                .expectStatus().value(status -> assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    void invalidJwt_isRejectedWith401() {
        org.mockito.Mockito.when(jwtDecoder.decode(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(Mono.error(new BadJwtException("bad token")));

        webTestClient.get().uri("/api/page/clients/jdoe/allInformation")
                .header(HttpHeaders.AUTHORIZATION, "Bearer garbage")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
