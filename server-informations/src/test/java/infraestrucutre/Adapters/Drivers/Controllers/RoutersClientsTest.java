package infraestrucutre.Adapters.Drivers.Controllers;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

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
import infraestrucutre.Adapters.Drivens.DTOS.DtoChangePassword;
import infraestrucutre.Adapters.Drivens.DTOS.DtoChangeUsername;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDeleteAccount;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.DetailUser;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Handlers.ClientHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class RoutersClientsTest {

    @Mock
    private ClientServiceInterface clientService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        ClientHandler handler = new ClientHandler(clientService);
        RouterFunction<ServerResponse> routes = new RoutersClients().clientRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private AllClient sampleClient() {
        return new AllClient(1L, "jdoe", "pw", "jdoe@test.com", "coach99", "John", "Q", "Doe", "Public",
                "30", "180", "80", "GOLD", LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1), 49.99);
    }

    @Test
    void createClient_returnsServiceResponse() {
        AllClient newClient = sampleClient();
        when(clientService.createClient(newClient)).thenReturn(Mono.just(List.of("Client created")));

        client.post().uri("/api/clients/AD/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newClient)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getAllClientsAD_defaultsPageAndSize() {
        when(clientService.getAllClientsAD(0, 10)).thenReturn(Flux.just(sampleClient()));

        client.get().uri("/api/clients/AD/allClients")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AllClient.class).hasSize(1);
    }

    @Test
    void getAllClientsAD_returns404_whenNoResults() {
        when(clientService.getAllClientsAD(0, 10)).thenReturn(Flux.empty());

        client.get().uri("/api/clients/AD/allClients")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getClientDetailMembershipByClientId_returnsMatch() {
        when(clientService.getClientDetailMembershipByClientId("jdoe")).thenReturn(Mono.just(sampleClient()));

        client.get().uri("/api/clients/jdoe/allInformation")
                .exchange()
                .expectStatus().isOk()
                .expectBody(AllClient.class).isEqualTo(sampleClient());
    }

    @Test
    void updateClient_returnsServiceResponse() {
        DetailUser detail = new DetailUser();
        detail.setName("John");
        when(clientService.updateClientAllDetailInformation("jdoe", detail)).thenReturn(Mono.just("Updated"));

        client.put().uri("/api/clients/jdoe/updateBasicInformation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(detail)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Updated");
    }

    @Test
    void updateClientPassword_readsOldAndNewPasswordFromBody_notFromUrl() {
        when(clientService.updateClientPassword("jdoe", "new-pw", "old-pw")).thenReturn(Mono.just("Password updated"));

        client.put().uri("/api/clients/jdoe/changePassword")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new DtoChangePassword("old-pw", "new-pw"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Password updated");
    }

    @Test
    void updateClientEmail_returnsServiceResponse() {
        when(clientService.updateClientEmail("jdoe", "new@test.com")).thenReturn(Mono.just("Email updated"));

        client.put().uri("/api/clients/jdoe/new@test.com/changeEmail")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Email updated");
    }

    @Test
    void updateClientUsername_readsPasswordFromBody_notFromUrl() {
        when(clientService.updateClientUsername("jdoe", "jdoe2", "pw")).thenReturn(Mono.just("Username updated"));

        client.put().uri("/api/clients/jdoe/changeUsername")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new DtoChangeUsername("jdoe2", "pw"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Username updated");
    }

    @Test
    void updateClientMembership_returnsServiceResponse() {
        when(clientService.updateClientMembership("jdoe", "GOLD")).thenReturn(Mono.just("Membership updated"));

        client.put().uri("/api/clients/jdoe/GOLD/assignMembership")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Membership updated");
    }

    @Test
    void updateTrainer_returnsServiceResponse() {
        when(clientService.updateTrainer("jdoe", "coach99")).thenReturn(Mono.just("Trainer assigned"));

        client.put().uri("/api/clients/jdoe/coach99/assignTrainer")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Trainer assigned");
    }

    @Test
    void dessignTrainer_returnsServiceResponse() {
        when(clientService.dessignTrainer("jdoe", "coach99")).thenReturn(Mono.just("Trainer removed"));

        client.delete().uri("/api/clients/jdoe/coach99/dessignTrainer")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Trainer removed");
    }

    @Test
    void dessignMembership_returnsServiceResponse() {
        when(clientService.dessignClientMembership("jdoe", "GOLD")).thenReturn(Mono.just("Membership removed"));

        client.delete().uri("/api/clients/jdoe/GOLD/dessignMembership")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Membership removed");
    }

    @Test
    void deleteClient_readsPasswordFromBody_notFromUrl() {
        when(clientService.deleteClient("jdoe", "s3cret")).thenReturn(Mono.just("Deleted"));

        client.method(org.springframework.http.HttpMethod.DELETE)
                .uri("/api/clients/jdoe/deleteAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new DtoDeleteAccount("s3cret"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Deleted");
    }

    @Test
    void getWorkClassesByClientId_returnsMatches() {
        WorkClass workClass = new WorkClass();
        workClass.setId(1L);
        workClass.setName("Yoga");
        when(clientService.getWorkClassesByClientId("jdoe")).thenReturn(Flux.just(workClass));

        client.get().uri("/api/clients/jdoe/workClasses")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void existsByUsernameClient_returnsBooleanFromService() {
        when(clientService.existsByUsernameClient("jdoe")).thenReturn(Mono.just(true));

        client.get().uri("/api/clients/jdoe/usernameExist")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(true);
    }

    @Test
    void existsByEmailClient_returnsBooleanFromService() {
        when(clientService.existsByEmailClient("jdoe@test.com")).thenReturn(Mono.just(false));

        client.get().uri("/api/clients/jdoe@test.com/emailExist")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(false);
    }
}
