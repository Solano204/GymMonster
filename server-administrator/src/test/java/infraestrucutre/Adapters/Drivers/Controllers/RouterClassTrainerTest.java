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

import infraestrucutre.Adapters.Drivens.DTOS.DtoChangePassword;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDeleteAccount;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Handlers.ClassTrainerHandler;
import infraestrucutre.Adapters.Drivens.ImpServices.ClassTrainerService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class RouterClassTrainerTest {

    @Mock
    private ClassTrainerService classTrainerService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        ClassTrainerHandler handler = new ClassTrainerHandler(classTrainerService);
        RouterFunction<ServerResponse> routes = new RouterClassTrainer().classTrainerRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private AllTrainer sampleTrainer() {
        return new AllTrainer(1L, "coach99", "pw", "coach@test.com", "Ana", "M", "Lopez", "Diaz",
                "35", "170", "65", LocalDate.now());
    }

    @Test
    void createClassTrainer_returnsServiceResponse() {
        AllTrainer trainer = sampleTrainer();
        when(classTrainerService.createClassTrainer(trainer)).thenReturn(Mono.just("Created"));

        client.post().uri("/api/admin/classTrainers/create")
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
        when(classTrainerService.getAllTrainers(0, 10)).thenReturn(Flux.just(trainerData));

        client.get().uri("/api/admin/classTrainers/all")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DtoTrainerData.class).hasSize(1);
    }

    @Test
    void getAllSpecialtiesFromTrainer_returnsMatches() {
        when(classTrainerService.getAllSpecialtiesFromTrainer("coach99"))
                .thenReturn(Mono.just(List.of(new DtoSpecialtyRecived("Yoga", "desc"))));

        client.get().uri("/api/admin/classTrainers/coach99/specialties")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateTrainerAllDetailInformation_returnsServiceResponse() {
        DtoDetailUserReciving details = new DtoDetailUserReciving("Ana", "M", "Lopez", "Diaz", "35", "65", "170");
        when(classTrainerService.updateTrainerAllDetailInformation("coach99", details)).thenReturn(Mono.just("Updated"));

        client.put().uri("/api/admin/classTrainers/coach99/updateBasicInformation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(details)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Updated");
    }

    @Test
    void getWorkClassesByTrainer_returnsMatches() {
        when(classTrainerService.getWorkClassesByTrainer("coach99"))
                .thenReturn(Mono.just(List.of(new WorkClass(1L, "Yoga", "desc", "60min"))));

        client.get().uri("/api/admin/classTrainers/coach99/workclasses")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateTrainerPassword_readsOldAndNewPasswordFromBody_notFromUrl() {
        when(classTrainerService.updateTrainerPassword("coach99", "old-pw", "new-pw"))
                .thenReturn(Mono.just("Password updated"));

        client.put().uri("/api/admin/classTrainers/coach99/password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new DtoChangePassword("old-pw", "new-pw"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Password updated");
    }

    @Test
    void updateTrainerUsername_returnsServiceResponse() {
        when(classTrainerService.updateTrainerUsername("coach99", "coach100")).thenReturn(Mono.just("Username updated"));

        client.put().uri("/api/admin/classTrainers/username/coach99/coach100")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Username updated");
    }

    @Test
    void updateTrainerEmail_returnsServiceResponse() {
        when(classTrainerService.updateEmail("coach99", "new@test.com")).thenReturn(Mono.just("Email updated"));

        client.put().uri("/api/admin/classTrainers/email/coach99/new@test.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Email updated");
    }

    @Test
    void addSpecialtyToTrainer_returnsServiceResponse() {
        when(classTrainerService.addSpecialtyToTrainer("coach99", "Yoga")).thenReturn(Mono.just("Specialty added"));

        client.put().uri("/api/admin/classTrainers/coach99/addSpecialty/Yoga")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Specialty added");
    }

    @Test
    void dessociateSpecialty_returnsServiceResponse() {
        when(classTrainerService.dessociateSpecialty("coach99", "Yoga")).thenReturn(Mono.just("Specialty removed"));

        client.delete().uri("/api/admin/classTrainers/coach99/removeSpecialty/Yoga")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Specialty removed");
    }

    @Test
    void addClassToTrainer_returnsServiceResponse() {
        when(classTrainerService.addClassToTrainer("coach99", "Yoga101")).thenReturn(Mono.just("Class added"));

        client.put().uri("/api/admin/classTrainers/coach99/addClass/Yoga101")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Class added");
    }

    @Test
    void dessociateClassToTrainer_returnsServiceResponse() {
        when(classTrainerService.dessociateClassToTrainer("coach99", "Yoga101")).thenReturn(Mono.just("Class removed"));

        client.delete().uri("/api/admin/classTrainers/coach99/removeClass/Yoga101")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Class removed");
    }

    @Test
    void deleteClassTrainerByUsernameWithPassword_readsPasswordFromBody_notFromUrl() {
        when(classTrainerService.deleteClassTrainerByUsernameWithPassword("coach99", "pw"))
                .thenReturn(Mono.just("Deleted"));

        client.method(org.springframework.http.HttpMethod.DELETE)
                .uri("/api/admin/classTrainers/coach99/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new DtoDeleteAccount("pw"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Deleted");
    }
}
