package infraestrucutre.Adapters.Drivers.Controllers;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.ClientServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoChangePassword;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDeleteAccount;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Handlers.ClientHandler;
import reactor.core.publisher.Mono;

/**
 * Covers every ClientRouter route not already exercised by ClientRouterTest (the
 * change-trainer regression test), plus ClientHandler's shared errorHandler behavior:
 * NOT_FOUND WebClientResponseException -> 404 JSON error body, other status codes ->
 * passthrough status, and any other exception -> 500.
 */
@ExtendWith(MockitoExtension.class)
class ClientRouterFullCoverageTest {

    @Mock
    private ClientServiceInterface clientService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        ClientHandler handler = new ClientHandler(clientService);
        RouterFunction<ServerResponse> routes = new ClientRouter().clientRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private AllClient sampleClient() {
        return new AllClient(1L, "jdoe", "pw", "jdoe@test.com", "coach99", "John", "Q", "Doe", "Public",
                "30", "180", "80", "GOLD", LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1), 49.99);
    }

    @Test
    void getAllClients_returnsTheClientListFromTheService() {
        when(clientService.getAllClients()).thenReturn(Mono.just(List.of(sampleClient())));

        client.get().uri("/api/admin/clients")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AllClient.class).hasSize(1);
    }

    @Test
    void getAllClass_returnsWorkClassesForTheGivenUsername() {
        when(clientService.getWorkClassesByClientId("jdoe"))
                .thenReturn(Mono.just(List.of(new WorkClass(1L, "Yoga", "desc", "60min"))));

        client.get().uri("/api/admin/clients/jdoe/allClass")
                .exchange()
                .expectStatus().isOk();

        verify(clientService).getWorkClassesByClientId("jdoe");
    }

    @Test
    void createClient_createsAndReturnsServiceResponse() {
        AllClient newClient = sampleClient();
        when(clientService.createClient(newClient)).thenReturn(Mono.just("Created"));

        client.post().uri("/api/admin/clients")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(newClient)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Created");
    }

    @Test
    void createClient_returns500_whenServiceFailsUnexpectedly() {
        AllClient newClient = sampleClient();
        when(clientService.createClient(newClient)).thenReturn(Mono.error(new RuntimeException("DB down")));

        client.post().uri("/api/admin/clients")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(newClient)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void updateClient_updatesAndReturnsServiceResponse() {
        DtoDetailUserSent dto = new DtoDetailUserSent("John", "Q", "Doe", "Public", "30", "80", "180");
        when(clientService.updateAllInformation(eq(dto), eq("jdoe"))).thenReturn(Mono.just("Updated"));

        client.put().uri("/api/admin/clients/jdoe")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Updated");
    }

    @Test
    void updateAllInformation_updatesAndReturnsServiceResponse() {
        DtoDetailUserSent dto = new DtoDetailUserSent("John", "Q", "Doe", "Public", "30", "80", "180");
        when(clientService.updateAllInformation(eq(dto), eq("jdoe"))).thenReturn(Mono.just("Updated"));

        client.put().uri("/api/admin/clients/jdoe/update-info")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Updated");
    }

    @Test
    void deleteClient_readsPasswordFromBody_notFromPathVariable() {
        when(clientService.deleteClient("jdoe", "s3cret")).thenReturn(Mono.just("Deleted"));

        client.method(org.springframework.http.HttpMethod.DELETE)
                .uri("/api/admin/clients/jdoe/deleteAccount")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(new DtoDeleteAccount("s3cret"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Deleted");

        verify(clientService).deleteClient("jdoe", "s3cret");
    }

    @Test
    void deleteClient_returns404JsonBody_whenServiceReportsClientNotFound() {
        when(clientService.deleteClient("ghost", "s3cret")).thenReturn(
                Mono.error(WebClientResponseException.create(
                        HttpStatus.NOT_FOUND.value(), "Not Found", HttpHeaders.EMPTY, new byte[0], null)));

        client.method(org.springframework.http.HttpMethod.DELETE)
                .uri("/api/admin/clients/ghost/deleteAccount")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(new DtoDeleteAccount("s3cret"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void deleteClient_passesThroughUpstreamStatus_forNonNotFoundErrors() {
        when(clientService.deleteClient("jdoe", "wrong-pw")).thenReturn(
                Mono.error(WebClientResponseException.create(
                        HttpStatus.BAD_REQUEST.value(), "Bad Request", HttpHeaders.EMPTY,
                        "Incorrect password".getBytes(), null)));

        client.method(org.springframework.http.HttpMethod.DELETE)
                .uri("/api/admin/clients/jdoe/deleteAccount")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(new DtoDeleteAccount("wrong-pw"))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void validateIfUserNameExists_returnsBooleanFromService() {
        when(clientService.validateIfUserNameExistsClient("jdoe")).thenReturn(Mono.just(true));

        client.get().uri("/api/admin/clients/validate/username/jdoe")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(true);
    }

    @Test
    void validateIfEmailExists_returnsBooleanFromService() {
        when(clientService.validateIfEmailExistsClient("jdoe@test.com")).thenReturn(Mono.just(false));

        client.get().uri("/api/admin/clients/validate/email/jdoe@test.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(false);
    }

    @Test
    void changePassword_readsOldAndNewPasswordFromBody_notFromUrl() {
        when(clientService.changePassword("jdoe", "old-pw", "new-pw")).thenReturn(Mono.just("Password changed"));

        client.put().uri("/api/admin/clients/jdoe/change-password")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(new DtoChangePassword("old-pw", "new-pw"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Password changed");

        verify(clientService).changePassword("jdoe", "old-pw", "new-pw");
    }

    @Test
    void changeEmail_updatesAndReturnsServiceResponse() {
        when(clientService.changeEmail("jdoe", "new@test.com")).thenReturn(Mono.just("Email changed"));

        client.put().uri("/api/admin/clients/jdoe/change-email/new@test.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Email changed");
    }

    @Test
    void changeUsername_updatesAndReturnsServiceResponse() {
        when(clientService.changeUsername("jdoe", "jdoe2")).thenReturn(Mono.just("Username changed"));

        client.put().uri("/api/admin/clients/jdoe/change-username/jdoe2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Username changed");
    }

    @Test
    void changeMembership_assignsAndReturnsServiceResponse() {
        when(clientService.changeMembership("jdoe", "GOLD")).thenReturn(Mono.just("Membership changed"));

        client.post().uri("/api/admin/clients/jdoe/GOLD/change-membership")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Membership changed");
    }

    @Test
    void removeMembership_unassignsAndReturnsServiceResponse() {
        when(clientService.removeMembership("jdoe", "GOLD")).thenReturn(Mono.just("Membership removed"));

        client.method(org.springframework.http.HttpMethod.DELETE)
                .uri("/api/admin/clients/jdoe/GOLD/delete-membership")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Membership removed");
    }

    @Test
    void removeTrainer_readsTrainerUsernamePathVariable_andReturnsServiceResponse() {
        when(clientService.removeTrainer("jdoe", "coach99")).thenReturn(Mono.just("Trainer removed"));

        client.method(org.springframework.http.HttpMethod.DELETE)
                .uri("/api/admin/clients/jdoe/coach99/remove-trainer")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Trainer removed");

        verify(clientService).removeTrainer("jdoe", "coach99");
    }
}
