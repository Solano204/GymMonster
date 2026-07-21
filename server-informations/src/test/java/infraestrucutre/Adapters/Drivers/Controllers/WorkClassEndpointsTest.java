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

import application.Ports.Drivers.IServices.WorkClassServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.DetailUser;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Handlers.WorkClassHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class WorkClassEndpointsTest {

    @Mock
    private WorkClassServiceInterface workClassService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        WorkClassHandler handler = new WorkClassHandler(workClassService);
        RouterFunction<ServerResponse> routes = new WorkClassEndpoints(handler).workClassRoutes();
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private WorkClass sample() {
        WorkClass workClass = new WorkClass();
        workClass.setId(1L);
        workClass.setName("Yoga");
        workClass.setDescription("desc");
        workClass.setDuration("60min");
        return workClass;
    }

    @Test
    void createWorkClass_returns201Created() {
        WorkClass workClass = sample();
        when(workClassService.createWorkClass(workClass)).thenReturn(Mono.just(workClass));

        client.post().uri("/api/workclasses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(workClass)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void getAllWorkClasses_returnsTheFullList() {
        when(workClassService.getAllWorkClasses()).thenReturn(Flux.just(sample()));

        client.get().uri("/api/workclasses")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(WorkClass.class).hasSize(1);
    }

    @Test
    void getWorkClassByName_returns404_whenNotFound() {
        when(workClassService.getWorkClassByName("Ghost")).thenReturn(Mono.empty());

        client.get().uri("/api/workclasses/Ghost")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateWorkClass_resolvesNameToIdBeforeUpdating() {
        WorkClass existing = sample();
        WorkClass incoming = new WorkClass();
        incoming.setName("Pilates");
        incoming.setDescription("new-desc");
        incoming.setDuration("45min");
        when(workClassService.getWorkClassByName("Yoga")).thenReturn(Mono.just(existing));
        when(workClassService.updateWorkClass(1L, incoming)).thenReturn(Mono.just(incoming));

        client.put().uri("/api/workclasses/Yoga")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(incoming)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteWorkClass_resolvesNameToIdBeforeDeleting() {
        WorkClass existing = sample();
        when(workClassService.getWorkClassByName("Yoga")).thenReturn(Mono.just(existing));
        when(workClassService.deleteWorkClass(1L)).thenReturn(Mono.empty());

        client.delete().uri("/api/workclasses/Yoga")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void getWorkClassSchedules_returnsSchedulesForTheGivenName() {
        when(workClassService.getWorkClassSchedules("Yoga")).thenReturn(Flux.just(new Schedule(1, "MONDAY", "08:00", "09:00")));

        client.get().uri("/api/workclasses/Yoga/schedules")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Schedule.class).hasSize(1);
    }

    @Test
    void getClientsByWorkClassWithPagination_defaultsPageAndSize() {
        WorkClass existing = sample();
        when(workClassService.getWorkClassByName("Yoga")).thenReturn(Mono.just(existing));
        when(workClassService.getClientsByWorkClassWithPagination("Yoga", 0, 10))
                .thenReturn(Flux.just(new DetailUser()));

        client.get().uri("/api/workclasses/Yoga/clients")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DetailUser.class).hasSize(1);
    }

    @Test
    void getTrainersByWorkClassWithPagination_defaultsPageAndSize() {
        WorkClass existing = sample();
        when(workClassService.getWorkClassByName("Yoga")).thenReturn(Mono.just(existing));
        when(workClassService.getTrainersByWorkClassWithPagination("Yoga", 0, 10))
                .thenReturn(Flux.just(new DetailUser()));

        client.get().uri("/api/workclasses/Yoga/trainers")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DetailUser.class).hasSize(1);
    }
}
