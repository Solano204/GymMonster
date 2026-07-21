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

import application.Ports.Drivers.IServices.ScheduleServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Handlers.ScheduleHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ScheduleRouterTest {

    @Mock
    private ScheduleServiceInterface scheduleService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        ScheduleHandler handler = new ScheduleHandler(scheduleService);
        RouterFunction<ServerResponse> routes = new ScheduleRouter(handler).scheduleRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private Schedule sample() {
        return new Schedule(1, "MONDAY", "08:00", "09:00");
    }

    @Test
    void createSchedule_returns201Created() {
        Schedule schedule = sample();
        when(scheduleService.createSchedule(schedule)).thenReturn(Mono.just(schedule));

        client.post().uri("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(schedule)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void getAllSchedules_returnsTheFullList() {
        when(scheduleService.getAllSchedules()).thenReturn(Flux.just(sample()));

        client.get().uri("/api/schedules")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Schedule.class).hasSize(1);
    }

    @Test
    void getScheduleByStartTime_returns404_whenEmpty() {
        when(scheduleService.getScheduleByStartTime("10:00")).thenReturn(Flux.empty());

        client.get().uri("/api/schedules/start-time/10:00")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getScheduleByDay_returnsMatches() {
        when(scheduleService.getScheduleByDay("MONDAY")).thenReturn(Flux.just(sample()));

        client.get().uri("/api/schedules/day/MONDAY")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Schedule.class).hasSize(1);
    }

    @Test
    void getScheduleByDayGym_returnsMatches_fromTheDistinctGymRoute() {
        when(scheduleService.getSchedulesByDayGym("MONDAY")).thenReturn(Flux.just(sample()));

        client.get().uri("/api/schedules/day/gym/MONDAY")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Schedule.class).hasSize(1);
    }

    @Test
    void updateSchedule_returnsTheUpdatedSchedule() {
        Schedule schedule = sample();
        when(scheduleService.updateSchedule(1, schedule)).thenReturn(Mono.just(schedule));

        client.put().uri("/api/schedules/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(schedule)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteSchedule_returns204NoContent() {
        when(scheduleService.deleteSchedule(1)).thenReturn(Mono.empty());

        client.delete().uri("/api/schedules/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
