package infraestrucutre.Adapters.Drivens.Configurations;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Same DEPENDS-on-external-services convention as RegisterServiceKafkaListenerIntegrationTest's
 * sibling tests in server-informations (needs docker-compose-test-inf.yaml running first).
 *
 * server-register's SecurityConfig has the identical broad-permitAll bug found across this whole
 * portfolio: .pathMatchers("/public/**", "*&#47;*,", "/**").permitAll() matches every path, making
 * .anyExchange().authenticated() dead code. server-register has no REST endpoints of its own
 * (it's Kafka-consumer-only), so this mainly matters for its actuator endpoints - but the tripwire
 * is worth having in case endpoints are ever added here.
 */
@ActiveProfiles("test")
// classes= is required here: this test's package (infraestrucutre.*) isn't nested under the
// app's own package (com.monster.server_register), so @SpringBootTest's default
// upward-package-scan for a @SpringBootConfiguration can never find ServerRegisterApplication.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = com.monster.server_register.ServerRegisterApplication.class)
@AutoConfigureWebTestClient
class SecurityConfigIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void everyPathIsCurrentlyPermittedWithoutAnyCredentials_dueToTheBroadPermitAllGlob() {
        webTestClient.get().uri("/actuator/auditevents")
                .exchange()
                .expectStatus().value(status -> assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value()));
    }
}
