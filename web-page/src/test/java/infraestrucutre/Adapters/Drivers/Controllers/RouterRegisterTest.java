package infraestrucutre.Adapters.Drivers.Controllers;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.ClientServiceInterface;
import infraestrucutre.Adapters.Drivens.Handlers.RegisterClientHandler;
import reactor.core.publisher.Mono;

/**
 * Pins the JSON contract for changePassword/deleteAccount after they moved from
 * path variables to a request body (credentials in the URL leak into browser
 * history, access logs and Referer headers). The frontend (monster-gym-frontend's
 * clientsApi) and the gateway/frontend endpoint explorer both assume this shape -
 * this test fails loudly if the route or the DTO field names drift again.
 */
@ExtendWith(MockitoExtension.class)
class RouterRegisterTest {

    @Mock
    private ClientServiceInterface clientService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        RegisterClientHandler handler = new RegisterClientHandler(clientService);
        RouterFunction<ServerResponse> routes = new RouterRegister().clientRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    @Test
    void changePassword_readsCredentialsFromJsonBody_notPathSegments() {
        when(clientService.updateClientPassword("jdoe", "newSecret1", "oldSecret1"))
                .thenReturn(Mono.just("Password updated"));

        client.put()
                .uri("/api/page/clients/jdoe/changePassword")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"oldPassword\":\"oldSecret1\",\"newPassword\":\"newSecret1\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Password updated");

        verify(clientService).updateClientPassword(eq("jdoe"), eq("newSecret1"), eq("oldSecret1"));
    }

    @Test
    void deleteAccount_readsPasswordFromJsonBody_notPathSegment() {
        when(clientService.deleteAccount("jdoe", "oldSecret1")).thenReturn(Mono.just("Account deleted"));

        client.method(org.springframework.http.HttpMethod.DELETE)
                .uri("/api/page/clients/jdoe/deleteAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"password\":\"oldSecret1\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Account deleted");

        verify(clientService).deleteAccount(eq("jdoe"), eq("oldSecret1"));
    }
}
