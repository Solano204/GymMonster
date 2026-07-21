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

import application.Ports.Drivers.IServices.ClassTrainerServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoTrainer;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.DetailsClassTrainer;
import infraestrucutre.Adapters.Drivens.Entities.Specialty;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Handlers.TrainerHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class TrainerRouterTest {

    @Mock
    private ClassTrainerServiceInterface trainerService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        TrainerHandler handler = new TrainerHandler(trainerService);
        RouterFunction<ServerResponse> routes = new TrainerRouter().trainerRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private AllTrainer sampleTrainer() {
        return new AllTrainer(1L, "ctcoach", "pw", "ctcoach@test.com", "Ana", "M", "Lopez", "Diaz",
                "35", "170", "65", LocalDate.now());
    }

    @Test
    void createPerTrainer_returnsOk_whenNoValidationErrors() {
        when(trainerService.createPerTrainer(sampleTrainer())).thenReturn(Mono.just(List.of()));

        client.post().uri("/trainers/AD/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleTrainer())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Trainer created successfully.");
    }

    @Test
    void createPerTrainer_returnsBadRequest_whenValidationErrorsExist() {
        when(trainerService.createPerTrainer(sampleTrainer())).thenReturn(Mono.just(List.of("username already exists")));

        client.post().uri("/trainers/AD/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleTrainer())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getAllClassTrainers_defaultsPageAndSize() {
        DtoInfoTrainer info = new DtoInfoTrainer(1L, "ctcoach", "ctcoach@test.com", "Ana", "M", "Lopez",
                "Diaz", "35", "170", "65", LocalDate.now(), 0);
        when(trainerService.getAllPerTrainersAllInformation(0, 10)).thenReturn(Flux.just(info));

        client.get().uri("/trainers/allInformation")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DtoInfoTrainer.class).hasSize(1);
    }

    @Test
    void getAllSpecialtyOfTrainer_returnsMatches() {
        when(trainerService.getAllSpecialtyOfTrainer("ctcoach")).thenReturn(Flux.just(new Specialty(5L, "Yoga", "desc")));

        client.get().uri("/trainers/specialties/ctcoach")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Specialty.class).hasSize(1);
    }

    @Test
    void getWorkClassesByTrainer_returnsMatches() {
        WorkClass workClass = new WorkClass();
        workClass.setId(1L);
        workClass.setName("Yoga");
        when(trainerService.getWorkClassesByTrainer("ctcoach")).thenReturn(Flux.just(workClass));

        client.get().uri("/trainers/classes/ctcoach")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(WorkClass.class).hasSize(1);
    }

    @Test
    void updateTrainerAllDetailInformation_returnsServiceResponse() {
        DetailsClassTrainer details = new DetailsClassTrainer();
        details.setName("Ana");
        when(trainerService.updateTrainerAllDetailInformation("ctcoach", details)).thenReturn(Mono.just("Data updated successfully"));

        client.put().uri("/trainers/AD/ctcoach/updateBasicInformation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(details)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Data updated successfully");
    }

    @Test
    void updateTrainerPassword_readsOldAndNewPasswordFromPathVariables() {
        when(trainerService.updateTrainerPassword("ctcoach", "new-pw", "old-pw")).thenReturn(Mono.just("Password updated successfully"));

        client.put().uri("/trainers/AD/ctcoach/old-pw/new-pw/updatePassword")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Password updated successfully");
    }

    @Test
    void updateTrainerUsername_returnsServiceResponse() {
        when(trainerService.updateTrainerUsername("ctcoach", "ctcoach2", "pw")).thenReturn(Mono.just("Username updated successfully"));

        client.put().uri("/trainers/AD/ctcoach/ctcoach2/pw/updateUsername")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Username updated successfully");
    }

    @Test
    void updateClientEmail_returnsServiceResponse() {
        when(trainerService.updateClientEmail("ctcoach", "new@test.com")).thenReturn(Mono.just("Email updated successfully"));

        client.put().uri("/trainers/AD/ctcoach/new@test.com/updateEmail")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Email updated successfully");
    }

    @Test
    void addSpecialtyToTrainer_returnsServiceResponse() {
        when(trainerService.addSpecialtyToTrainer("ctcoach", "Yoga")).thenReturn(Mono.just("Specialty added successfully"));

        client.post().uri("/trainers/AD/ctcoach/Yoga/associateSpecialty")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Specialty added successfully");
    }

    @Test
    void dessociateSpecialty_returnsServiceResponse() {
        when(trainerService.dessociateSpecialty("ctcoach", "Yoga")).thenReturn(Mono.just("Specialty removed successfully"));

        client.delete().uri("/trainers/AD/ctcoach/Yoga/dessociateSpecialty")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Specialty removed successfully");
    }

    @Test
    void addClassToTrainer_returnsServiceResponse() {
        when(trainerService.addClassToTrainer("ctcoach", "Yoga101")).thenReturn(Mono.just("Class removed successfully"));

        client.post().uri("/trainers/AD/ctcoach/Yoga101/addClass")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void dessociateClassToTrainer_returnsServiceResponse() {
        when(trainerService.dessociateClassToTrainer("ctcoach", "Yoga101")).thenReturn(Mono.just("Class removed successfully"));

        client.delete().uri("/trainers/AD/ctcoach/Yoga101/dessociateClass")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteClassTrainerByUsernameWithPassword_returnsServiceResponse() {
        when(trainerService.deleteClassTrainerUsername("ctcoach", "pw")).thenReturn(Mono.just("account deleted successfully"));

        client.delete().uri("/trainers/AD/ctcoach/pw/delete")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("account deleted successfully");
    }
}
