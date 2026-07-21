package infraestrucutre.Adapters.Drivers.Controllers;

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

import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Handlers.WorkClassHandler;
import infraestrucutre.Adapters.Drivens.ImpServices.WorkClassService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class RouterWorkClassTest {

    @Mock
    private WorkClassService workClassService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        WorkClassHandler handler = new WorkClassHandler(workClassService);
        RouterFunction<ServerResponse> routes = new RouterWorkClass().routeWorkClass(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private WorkClass sampleWorkClass() {
        return new WorkClass(1L, "Yoga", "Relaxing class", "60min");
    }

    @Test
    void getAllWorkClasses_returnsTheFullList() {
        when(workClassService.getAllWorkClasses()).thenReturn(Flux.just(sampleWorkClass()));

        client.get().uri("/api/admin/work-classes/all")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(WorkClass.class).hasSize(1);
    }

    @Test
    void getWorkClassSchedules_returnsSchedulesForTheGivenName() {
        when(workClassService.getCSchedulesByWorkClassWithPagination("Yoga"))
                .thenReturn(Flux.just(new Schedule(1, "MONDAY", "08:00", "09:00")));

        client.get().uri("/api/admin/work-class/Yoga/schedules")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Schedule.class).hasSize(1);
    }

    @Test
    void getClientsByWorkClass_defaultsPageAndSize_whenQueryParamsAreMissing() {
        when(workClassService.getClientsByWorkClassWithPagination("Yoga", 0, 10))
                .thenReturn(Flux.just(new DtoDetailUserReciving("John", "Q", "Doe", "Public", "30", "80", "180")));

        client.get().uri("/api/admin/work-class/Yoga/clients")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DtoDetailUserReciving.class).hasSize(1);
    }

    @Test
    void getClientsByWorkClass_usesProvidedPageAndSizeQueryParams() {
        when(workClassService.getClientsByWorkClassWithPagination("Yoga", 2, 25))
                .thenReturn(Flux.just(new DtoDetailUserReciving("John", "Q", "Doe", "Public", "30", "80", "180")));

        client.get().uri("/api/admin/work-class/Yoga/clients?page=2&size=25")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DtoDetailUserReciving.class).hasSize(1);
    }

    @Test
    void getTrainersByWorkClass_defaultsPageAndSize_whenQueryParamsAreMissing() {
        when(workClassService.getTrainersByWorkClassWithPagination("Yoga", 0, 10))
                .thenReturn(Flux.just(new DtoDetailUserReciving("Coach", "Q", "Doe", "Public", "30", "80", "180")));

        client.get().uri("/api/admin/work-class/Yoga/trainers")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DtoDetailUserReciving.class).hasSize(1);
    }

    @Test
    void createWorkClass_returnsServiceResponse() {
        WorkClass workClass = sampleWorkClass();
        when(workClassService.createWorkclass(workClass)).thenReturn(Mono.just(workClass));

        client.post().uri("/api/admin/work-class/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(workClass)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateWorkClass_returnsServiceResponse() {
        WorkClass workClass = sampleWorkClass();
        when(workClassService.updateWorkClass(workClass, "Yoga")).thenReturn(Mono.just(workClass));

        client.put().uri("/api/admin/work-class/Yoga/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(workClass)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteWorkClass_returnsConfirmationMessage() {
        when(workClassService.deleteWorkClass("Yoga")).thenReturn(Mono.just("Deleted"));

        client.delete().uri("/api/admin/work-class/Yoga/delete")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Deleted");
    }
}
