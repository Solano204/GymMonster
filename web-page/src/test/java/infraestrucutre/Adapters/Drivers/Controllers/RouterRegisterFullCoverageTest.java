package infraestrucutre.Adapters.Drivers.Controllers;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.ClientServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Handlers.RegisterClientHandler;
import reactor.core.publisher.Mono;

/**
 * Covers every RouterRegister route not already exercised by RouterRegisterTest
 * (changePassword/deleteAccount's credentials-in-body contract), plus RegisterClientHandler's
 * shared errorHandler behavior.
 */
@ExtendWith(MockitoExtension.class)
class RouterRegisterFullCoverageTest {

    @Mock
    private ClientServiceInterface clientService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        RegisterClientHandler handler = new RegisterClientHandler(clientService);
        RouterFunction<ServerResponse> routes = new RouterRegister().clientRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private AllClient sampleClient() {
        return new AllClient(1L, "jdoe", "pw", "jdoe@test.com", "coach99", "John", "Q", "Doe", "Public",
                "30", "180", "80", "GOLD", LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1), 49.99);
    }

    @Test
    void createClient_returns201Created() {
        AllClient newClient = sampleClient();
        when(clientService.createClient(newClient)).thenReturn(Mono.just(List.of("Registered")));

        client.post().uri("/api/page/registerClient")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newClient)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void createClient_returns500_whenServiceReturnsEmpty() {
        AllClient newClient = sampleClient();
        when(clientService.createClient(newClient)).thenReturn(Mono.empty());

        client.post().uri("/api/page/registerClient")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newClient)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void updateClientAllInformation_returnsServiceResponse() {
        DtoDetailUserSent dto = new DtoDetailUserSent("John", "Q", "Doe", "Public", "30", "80", "180");
        when(clientService.updateClientAllDetailInformation("jdoe", dto)).thenReturn(Mono.just("Updated"));

        client.put().uri("/api/page/clients/jdoe/changeInformation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Updated");
    }

    @Test
    void updateClientUsername_returnsServiceResponse() {
        when(clientService.updateClientUsername("jdoe", "jdoe2")).thenReturn(Mono.just("Username changed"));

        client.put().uri("/api/page/clients/jdoe/jdoe2/changeUsername")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Username changed");
    }

    @Test
    void updateClientEmail_returnsServiceResponse() {
        when(clientService.updateClientEmail("jdoe", "new@test.com")).thenReturn(Mono.just("Email changed"));

        client.put().uri("/api/page/clients/jdoe/new@test.com/changeEmail")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Email changed");
    }

    @Test
    void updateClientMembership_returnsServiceResponse() {
        when(clientService.updateClientMembership("jdoe", "GOLD")).thenReturn(Mono.just("Membership changed"));

        client.put().uri("/api/page/clients/jdoe/GOLD/changeMembership")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Membership changed");
    }

    @Test
    void updateClientTrainer_returnsServiceResponse() {
        when(clientService.updateClientTrainer("jdoe", "coach2")).thenReturn(Mono.just("Trainer changed"));

        client.put().uri("/api/page/clients/jdoe/coach2/changeTrainer")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Trainer changed");
    }

    @Test
    void removeMembership_returnsServiceResponse() {
        when(clientService.removeMembership("jdoe", "GOLD")).thenReturn(Mono.just("Membership removed"));

        client.delete().uri("/api/page/clients/jdoe/GOLD/delete-membership")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Membership removed");
    }

    @Test
    void removeTrainer_returnsServiceResponse() {
        when(clientService.removeTrainer("jdoe", "coach99")).thenReturn(Mono.just("Trainer removed"));

        client.delete().uri("/api/page/clients/jdoe/coach99/remove-trainer")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Trainer removed");
    }

    @Test
    void getAllClass_returnsWorkClassesForTheUsername() {
        WorkClass workClass = new WorkClass(1L, "Yoga", "desc", "60min");
        when(clientService.getWorkClassesByClientId("jdoe")).thenReturn(Mono.just(List.of(workClass)));

        client.get().uri("/api/page/clients/jdoe/allClass")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(WorkClass.class).hasSize(1);
    }

    @Test
    void getClient_returnsClientData() {
        AllClient allClient = sampleClient();
        when(clientService.getClient("jdoe")).thenReturn(Mono.just(allClient));

        client.get().uri("/api/page/clients/jdoe/allInformation")
                .exchange()
                .expectStatus().isOk()
                .expectBody(AllClient.class).isEqualTo(allClient);
    }

    @Test
    void getClient_returns404_whenServiceReportsClientNotFound() {
        when(clientService.getClient("ghost")).thenReturn(
                Mono.error(WebClientResponseException.create(
                        HttpStatus.NOT_FOUND.value(), "Not Found", HttpHeaders.EMPTY, new byte[0], null)));

        client.get().uri("/api/page/clients/ghost/allInformation")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
    }
}
