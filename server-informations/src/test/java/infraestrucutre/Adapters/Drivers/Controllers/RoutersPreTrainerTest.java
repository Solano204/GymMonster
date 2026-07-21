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

import application.Ports.Drivers.IServices.PerTrainerServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDeleteAccount;
import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoTrainer;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.DetailPerTrainer;
import infraestrucutre.Adapters.Drivens.Entities.Specialty;
import infraestrucutre.Adapters.Drivens.Handlers.PerTrainerHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class RoutersPreTrainerTest {

    @Mock
    private PerTrainerServiceInterface perTrainerService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        PerTrainerHandler handler = new PerTrainerHandler(perTrainerService);
        RouterFunction<ServerResponse> routes = new RoutersPreTrainer().perTrainersRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private AllTrainer sampleTrainer() {
        return new AllTrainer(1L, "pt1", "pw", "pt1@test.com", "Ana", "M", "Lopez", "Diaz",
                "35", "170", "65", LocalDate.now());
    }

    @Test
    void createPerTrainer_delegatesToService() {
        when(perTrainerService.createPerTrainer(sampleTrainer())).thenReturn(Mono.just(List.of()));

        client.post().uri("/api/pertrainers/AD/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleTrainer())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getAllPerTrainersAllInformation_returnsMatches() {
        DtoInfoTrainer info = new DtoInfoTrainer(1L, "pt1", "pt1@test.com", "Ana", "M", "Lopez",
                "Diaz", "35", "170", "65", LocalDate.now(), 0);
        when(perTrainerService.getAllPerTrainersAllInformation(0, 10)).thenReturn(Flux.just(info));

        client.get().uri("/api/pertrainers/allInformation")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DtoInfoTrainer.class).hasSize(1);
    }

    @Test
    void deletePerTrainer_readsPasswordFromBody_notFromUrl() {
        when(perTrainerService.deletePerTrainerUsername("pt1", "s3cret")).thenReturn(Mono.just("account deleted successfully"));

        client.method(org.springframework.http.HttpMethod.DELETE)
                .uri("/api/pertrainers/AD/pt1/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new DtoDeleteAccount("s3cret"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("account deleted successfully");
    }

    @Test
    void getAllClients_defaultsPageAndSize() {
        when(perTrainerService.getAllClients("pt1", 0, 10)).thenReturn(Flux.just(
                new infraestrucutre.Adapters.Drivens.Entities.DetailUser()));

        client.get().uri("/api/pertrainers/AllClients/pt1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getAllSpecialties_returnsMatches() {
        when(perTrainerService.getAllSpecialties("pt1")).thenReturn(Flux.just(new Specialty(5L, "Yoga", "desc")));

        client.get().uri("/api/pertrainers/pt1/specialties")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Specialty.class).hasSize(1);
    }

    @Test
    void updateTrainerDetails_returnsServiceResponse() {
        DetailPerTrainer details = new DetailPerTrainer();
        details.setName("Ana");
        when(perTrainerService.updateTrainerAllDetailInformation("pt1", details)).thenReturn(Mono.just("Data updated successfully"));

        client.put().uri("/api/pertrainers/pt1/AD/updateBasicInformation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(details)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Data updated successfully");
    }

    @Test
    void updateTrainerPassword_readsOldAndNewPasswordFromBody_notFromUrl() {
        when(perTrainerService.updateTrainerPassword("pt1", "new-pw", "old-pw")).thenReturn(Mono.just("Password updated successfully"));

        client.put().uri("/api/pertrainers/AD/pt1/updatePassword")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new infraestrucutre.Adapters.Drivens.DTOS.DtoChangePassword("old-pw", "new-pw"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Password updated successfully");
    }

    @Test
    void updateEmail_returnsServiceResponse() {
        when(perTrainerService.updateClientEmail("pt1", "new@test.com")).thenReturn(Mono.just("Email updated successfully"));

        client.put().uri("/api/pertrainers/AD/pt1/new@test.com/updateEmail")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Email updated successfully");
    }

    @Test
    void updateTrainerUsername_readsPasswordFromBody_notFromUrl() {
        when(perTrainerService.updateTrainerUsername("pt1", "pt2", "pw")).thenReturn(Mono.just("Username updated successfully"));

        client.put().uri("/api/pertrainers/AD/pt1/pt2/updateUsername")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new infraestrucutre.Adapters.Drivens.DTOS.DtoChangeUsername("pt2", "pw"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Username updated successfully");
    }

    @Test
    void addSpecialty_returnsServiceResponse() {
        when(perTrainerService.addSpecialtyToTrainer("pt1", "Yoga")).thenReturn(Mono.just("Specialty added successfully"));

        client.post().uri("/api/pertrainers/AD/pt1/Yoga/assignSpecialty")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Specialty added successfully");
    }

    @Test
    void removeSpecialty_returnsServiceResponse() {
        when(perTrainerService.dessociateSpecialty("pt1", "Yoga")).thenReturn(Mono.just("Specialty removed successfully"));

        client.delete().uri("/api/pertrainers/AD/pt1/Yoga/dessingSpecialty")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Specialty removed successfully");
    }
}
