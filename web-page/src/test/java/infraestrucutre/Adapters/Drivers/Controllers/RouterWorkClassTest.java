package infraestrucutre.Adapters.Drivers.Controllers;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.WorkClassInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Handlers.WorkClassHandler;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
class RouterWorkClassTest {

    @Mock
    private WorkClassInterface workClassService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        WorkClassHandler handler = new WorkClassHandler(workClassService);
        RouterFunction<ServerResponse> routes = new RouterWorkClass().workClassRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    @Test
    void getAllWorkClasses_returnsTheFullList() {
        when(workClassService.getAllWorkClasses()).thenReturn(Flux.just(new WorkClass(1L, "Yoga", "desc", "60min")));

        client.get().uri("/api/page/workclasses")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(WorkClass.class).hasSize(1);
    }

    @Test
    void getWorkClassSchedules_returnsSchedulesForTheGivenName() {
        when(workClassService.getWorkClassSchedules("Yoga"))
                .thenReturn(Flux.just(new Schedule(1, "MONDAY", "08:00", "09:00")));

        client.get().uri("/api/page/workclasses/Yoga/schedules")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Schedule.class).hasSize(1);
    }

    @Test
    void getClientsByWorkClass_defaultsPageAndSize_whenQueryParamsAreMissing() {
        when(workClassService.getClientsByWorkClassWithPagination("Yoga", 0, 10))
                .thenReturn(Flux.just(new DtoDetailUserReciving("John", "Q", "Doe", "Public", "30")));

        client.get().uri("/api/page/workclasses/Yoga/clients")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DtoDetailUserReciving.class).hasSize(1);
    }

    @Test
    void getClientsByWorkClass_usesProvidedPageAndSizeQueryParams() {
        when(workClassService.getClientsByWorkClassWithPagination("Yoga", 2, 25))
                .thenReturn(Flux.just(new DtoDetailUserReciving("John", "Q", "Doe", "Public", "30")));

        client.get().uri("/api/page/workclasses/Yoga/clients?page=2&size=25")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DtoDetailUserReciving.class).hasSize(1);
    }

    @Test
    void getTrainersByWorkClass_defaultsPageAndSize_whenQueryParamsAreMissing() {
        when(workClassService.getTrainersByWorkClassWithPagination("Yoga", 0, 10))
                .thenReturn(Flux.just(new DtoDetailUserReciving("Coach", "Q", "Doe", "Public", "30")));

        client.get().uri("/api/page/workclasses/Yoga/trainers")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DtoDetailUserReciving.class).hasSize(1);
    }
}
