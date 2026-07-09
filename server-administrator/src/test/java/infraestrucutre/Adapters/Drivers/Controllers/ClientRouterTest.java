package infraestrucutre.Adapters.Drivers.Controllers;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.ClientServiceInterface;
import infraestrucutre.Adapters.Drivens.Handlers.ClientHandler;
import reactor.core.publisher.Mono;

/**
 * Regression test for the change-trainer route: the handler used to read
 * request.pathVariable("") instead of "usernameTrainer", which threw
 * IllegalArgumentException on every call.
 */
@ExtendWith(MockitoExtension.class)
class ClientRouterTest {

    @Mock
    private ClientServiceInterface clientService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        ClientHandler handler = new ClientHandler(clientService);
        RouterFunction<ServerResponse> routes = new ClientRouter().clientRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    @Test
    void changeTrainer_extractsBothPathVariablesAndCallsService() {
        when(clientService.changeTrainer("jdoe", "coach99")).thenReturn(Mono.just("Trainer changed"));

        client.post()
                .uri("/api/admin/clients/jdoe/coach99/change-trainer")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Trainer changed");

        verify(clientService).changeTrainer(eq("jdoe"), eq("coach99"));
    }
}
