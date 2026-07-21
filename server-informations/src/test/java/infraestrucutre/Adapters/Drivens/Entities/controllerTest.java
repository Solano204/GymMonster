package infraestrucutre.Adapters.Drivens.Entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * "controller" is a stray @RestController left sitting in the Entities package (not
 * Adapters/Drivers/Controllers like every other router/handler in this codebase) - looks like
 * leftover scaffolding, but it's still a live, auto-detected Spring bean exposing GET /hello.
 */
class controllerTest {

    private final controller helloController = new controller();

    @Test
    void hello_returnsGreetingWithHardcodedName() {
        assertThat(helloController.hello()).isEqualTo("Hello JJ!");
    }

    @Test
    void hello_isWiredUpAsALiveWebFluxEndpoint() {
        WebTestClient.bindToController(new controller()).build()
                .get().uri("/hello")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello JJ!");
    }
}
