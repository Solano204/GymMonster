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

import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Handlers.ScheduleHandler;
import infraestrucutre.Adapters.Drivens.ImpServices.ScheduleService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ScheduleRouterTest {

    @Mock
    private ScheduleService scheduleService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        ScheduleHandler handler = new ScheduleHandler(scheduleService);
        RouterFunction<ServerResponse> routes = new ScheduleRouter(handler).routesSchedule();
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private Schedule sample() {
        return new Schedule(1, "MONDAY", "08:00", "09:00");
    }

    @Test
    void createSchedule_returnsTheCreatedSchedule() {
        Schedule schedule = sample();
        when(scheduleService.createSchedule(schedule)).thenReturn(Mono.just(schedule));

        client.post().uri("/api/admin/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(schedule)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Schedule.class).isEqualTo(schedule);
    }

    @Test
    void createSchedule_returns500_whenServiceFails() {
        Schedule schedule = sample();
        when(scheduleService.createSchedule(schedule)).thenReturn(Mono.error(new RuntimeException("boom")));

        client.post().uri("/api/admin/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(schedule)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void getAllSchedules_returnsTheFullList() {
        when(scheduleService.getAllSchedules()).thenReturn(Flux.just(sample()));

        client.get().uri("/api/admin/schedules")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Schedule.class).hasSize(1);
    }

    @Test
    void getScheduleByStartTime_returnsMatches() {
        when(scheduleService.getScheduleByStartTime("08:00")).thenReturn(Flux.just(sample()));

        client.get().uri("/api/admin/schedules/start/08:00")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Schedule.class).hasSize(1);
    }

    @Test
    void getScheduleByDay_returnsMatches() {
        when(scheduleService.getScheduleByDay("MONDAY")).thenReturn(Flux.just(sample()));

        client.get().uri("/api/admin/schedules/day/MONDAY")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Schedule.class).hasSize(1);
    }

    @Test
    void getScheduleByDayGym_returnsMatches_fromTheDistinctGymRoute() {
        when(scheduleService.getScheduleByDayGym("MONDAY")).thenReturn(Flux.just(sample()));

        client.get().uri("/api/admin/schedules/gym/day/MONDAY")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Schedule.class).hasSize(1);
    }

    @Test
    void updateSchedule_returnsTheUpdatedSchedule() {
        Schedule schedule = sample();
        when(scheduleService.updateSchedule(1, schedule)).thenReturn(Mono.just(schedule));

        client.put().uri("/api/admin/schedules/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(schedule)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Schedule.class).isEqualTo(schedule);
    }

    @Test
    void deleteSchedule_returnsServiceResponse() {
        when(scheduleService.deleteSchedule(1)).thenReturn(Mono.just("Deleted"));

        client.delete().uri("/api/admin/schedules/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Deleted");
    }
}
