package infraestrucutre.Adapters.Drivens.Security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Follows the same "DEPENDS on external services" convention as ClientRepositoryTest/
 * KeycloakServiceImplIntegrationTest (see "Steps to Test with Server-Configuration.txt" -
 * requires docker-compose-test-inf.yaml running first).
 *
 * This pins server-informations' SecurityConfig as CURRENTLY (and almost certainly
 * unintentionally) allowing every single request unauthenticated:
 *
 *   .pathMatchers("/public/**", "*&#47;*,", "/**").permitAll()
 *   .anyExchange().authenticated()
 *
 * The "/**" glob in the permitAll list matches every path, so .anyExchange().authenticated()
 * is dead code - identical to the bug already found and fixed in every other GYM_MOSTER
 * service (gateway/server-administrator SecurityConfigs both had this exact glob before their
 * respective fixes; see TESTING_NOTES.md). Also note: unlike gateway/server-administrator, this
 * SecurityConfig never calls .oauth2ResourceServer(...) at all - JwtAuthenticationConverter.java
 * in this module is entirely commented-out dead code, so even if the permitAll bug were fixed,
 * there is currently no JWT validation wired up to authenticate anyone with.
 *
 * These tests intentionally assert the (broken) status quo - "protected" endpoints currently
 * return non-401 with zero credentials - so a future fix that actually closes this hole will
 * fail these tests loudly, which is the point: it's a tripwire, not an endorsement.
 */
@ActiveProfiles("test")
// classes= is required here: this test's package (infraestrucutre.*) isn't nested under the
// app's own package (com.monster.server_informations), so @SpringBootTest's default
// upward-package-scan for a @SpringBootConfiguration can never find ServerInformationsApplication.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = com.monster.server_informations.ServerInformationsApplication.class)
@AutoConfigureWebTestClient
class SecurityConfigIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void everyPathIsCurrentlyPermittedWithoutAnyCredentials_dueToTheBroadPermitAllGlob() {
        webTestClient.get().uri("/api/clients/AD/allClients")
                .exchange()
                .expectStatus().value(status -> assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    void evenAnAdminOnlyLookingPathIsCurrentlyPermitted_becauseThereIsNoJwtValidationWiredUpAtAll() {
        webTestClient.get().uri("/api/pertrainers/allInformation")
                .exchange()
                .expectStatus().value(status -> assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value()));
    }
}
