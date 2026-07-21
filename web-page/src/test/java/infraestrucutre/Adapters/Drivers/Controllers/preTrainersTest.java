package infraestrucutre.Adapters.Drivers.Controllers;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.TrainerInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.Handlers.TrainerHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class preTrainersTest {

    @Mock
    private TrainerInterface trainerService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        TrainerHandler handler = new TrainerHandler(trainerService);
        RouterFunction<ServerResponse> routes = new preTrainers().trainerRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    @Test
    void getAllTrainers_defaultsPageAndSize_whenQueryParamsAreMissing() {
        DtoTrainerData trainerData = new DtoTrainerData(1L, "coach99", "coach@test.com", "Ana", "M", "Lopez",
                "Diaz", "35", "170", "65", null, 5);
        when(trainerService.getAllPerTrainers(0, 10)).thenReturn(Flux.just(trainerData));

        client.get().uri("/api/page/allTrainers")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getAllTrainers_usesProvidedPageAndSizeQueryParams() {
        DtoTrainerData trainerData = new DtoTrainerData(1L, "coach99", "coach@test.com", "Ana", "M", "Lopez",
                "Diaz", "35", "170", "65", null, 5);
        when(trainerService.getAllPerTrainers(2, 25)).thenReturn(Flux.just(trainerData));

        client.get().uri("/api/page/allTrainers?page=2&size=25")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getAllSpecialtiesFromTrainer_returnsMatches() {
        when(trainerService.getAllSpecialtiesFromTrainer("coach99"))
                .thenReturn(Mono.just(List.of(new DtoSpecialtyRecived("Yoga", "desc"))));

        client.get().uri("/api/page/trainers/coach99/specialties")
                .exchange()
                .expectStatus().isOk();
    }
}
