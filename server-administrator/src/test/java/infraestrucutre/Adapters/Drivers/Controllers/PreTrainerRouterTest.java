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

import application.Ports.Drivers.IServices.PerTrainerInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Handlers.PreTrainerHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class PreTrainerRouterTest {

    @Mock
    private PerTrainerInterface perTrainerService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        PreTrainerHandler handler = new PreTrainerHandler(perTrainerService);
        RouterFunction<ServerResponse> routes = new PreTrainerRouter().routeTrainer(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private AllTrainer sampleTrainer() {
        return new AllTrainer(1L, "coach99", "pw", "coach@test.com", "Ana", "M", "Lopez", "Diaz",
                "35", "170", "65", LocalDate.now());
    }

    @Test
    void createPerTrainer_returnsServiceResponse() {
        AllTrainer trainer = sampleTrainer();
        when(perTrainerService.createPerTrainer(trainer)).thenReturn(Mono.just("Created"));

        client.post().uri("/api/admin/trainer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(trainer)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Created");
    }

    @Test
    void getAllTrainers_defaultsPageAndSize_whenQueryParamsAreMissing() {
        DtoTrainerData trainerData = new DtoTrainerData(1L, "coach99", "coach@test.com", "Ana", "M", "Lopez",
                "Diaz", "35", "170", "65", LocalDate.now(), 5);
        when(perTrainerService.getAllTrainers(0, 10)).thenReturn(Flux.just(trainerData));

        client.get().uri("/api/admin/allTrainer")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DtoTrainerData.class).hasSize(1);
    }

    @Test
    void deletePerTrainer_readsUsernameAndPasswordFromPathVariables() {
        when(perTrainerService.deletePerTrainer("coach99", "pw")).thenReturn(Mono.just("Deleted"));

        client.delete().uri("/api/admin/trainer/coach99/pw")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Deleted");
    }

    @Test
    void getAllClientsFromTrainer_defaultsPageAndSize() {
        when(perTrainerService.getAllClientsFromTrainer("coach99", 0, 10))
                .thenReturn(Flux.just(new DtoDetailUserReciving("John", "Q", "Doe", "Public", "30", "80", "180")));

        client.get().uri("/api/admin/trainer/coach99/allClients")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DtoDetailUserReciving.class).hasSize(1);
    }

    @Test
    void getAllSpecialtiesFromTrainer_returnsMatches() {
        when(perTrainerService.getAllSpecialtiesFromTrainer("coach99"))
                .thenReturn(Mono.just(List.of(new DtoSpecialtyRecived("Yoga", "desc"))));

        client.get().uri("/api/admin/trainer/coach99/specialties")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateTrainerPassword_readsUsernameNewPasswordAndOldPasswordFromPathVariables() {
        when(perTrainerService.updateTrainerPassword("coach99", "new-pw", "old-pw"))
                .thenReturn(Mono.just("Password updated"));

        client.put().uri("/api/admin/trainer/coach99/password/new-pw/old-pw")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Password updated");
    }

    @Test
    void updateEmail_returnsServiceResponse() {
        when(perTrainerService.updateEmail("coach99", "new@test.com")).thenReturn(Mono.just("Email updated"));

        client.put().uri("/api/admin/trainer/coach99/email/new@test.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Email updated");
    }

    @Test
    void updateTrainerUsername_returnsServiceResponse() {
        when(perTrainerService.updateTrainerUsername("coach99", "coach100")).thenReturn(Mono.just("Username updated"));

        client.put().uri("/api/admin/trainer/coach99/username/coach100")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Username updated");
    }

    @Test
    void updateTrainerDetails_returnsServiceResponse() {
        DtoDetailUserReciving details = new DtoDetailUserReciving("Ana", "M", "Lopez", "Diaz", "35", "65", "170");
        when(perTrainerService.updateTrainerDetails("coach99", details)).thenReturn(Mono.just("Updated"));

        client.put().uri("/api/admin/trainer/coach99/BasicInformation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(details)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Updated");
    }

    @Test
    void addSpecialtyToTrainer_returnsServiceResponse() {
        when(perTrainerService.addSpecialtyToTrainer("coach99", "Yoga")).thenReturn(Mono.just("Specialty added"));

        client.post().uri("/api/admin/trainer/coach99/addSpecialty/Yoga")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Specialty added");
    }

    @Test
    void removeSpecialtyFromTrainer_returnsServiceResponse() {
        when(perTrainerService.removeSpecialtyFromTrainer("coach99", "Yoga")).thenReturn(Mono.just("Specialty removed"));

        client.delete().uri("/api/admin/trainer/coach99/removeSpecialty/Yoga")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Specialty removed");
    }
}
