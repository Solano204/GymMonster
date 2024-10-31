package infraestrucutre.Adapters.Drivens.Handlers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.ImpServices.ScheduleService;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class ScheduleHandler {

    private final ScheduleService scheduleService;

   

    // Handler to create a new schedule
    public Mono<ServerResponse> createSchedule(ServerRequest request) {
        return request.bodyToMono(Schedule.class)
                .flatMap(schedule -> scheduleService.createSchedule(schedule)
                        .flatMap(createdSchedule -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(createdSchedule))
                        .onErrorResume(error -> errorHandler(Mono.error(error))));
    }

    // Handler to get all schedules
    public Mono<ServerResponse> getAllSchedules(ServerRequest request) {
        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(scheduleService.getAllSchedules(), Schedule.class));
    }

    // Handler to get a schedule by start time
    public Mono<ServerResponse> getScheduleByStartTime(ServerRequest request) {
        String startTime = request.pathVariable("startTime");

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(scheduleService.getScheduleByStartTime(startTime), Schedule.class));
    }

    // Handler to get schedules by day
    public Mono<ServerResponse> getScheduleByDay(ServerRequest request) {
        String day = request.pathVariable("day");

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(scheduleService.getScheduleByDay(day), Schedule.class));
    }

    // Handler to get schedules by day for a gym
    public Mono<ServerResponse> getScheduleByDayGym(ServerRequest request) {
        String day = request.pathVariable("day");

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(scheduleService.getScheduleByDayGym(day), Schedule.class));
    }

    // Handler to update a schedule by ID
    public Mono<ServerResponse> updateSchedule(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));

        return request.bodyToMono(Schedule.class)
                .flatMap(updatedSchedule -> scheduleService.updateSchedule(id, updatedSchedule)
                        .flatMap(schedule -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(schedule))
                        .onErrorResume(error -> errorHandler(Mono.error(error))));
    }

    // Handler to delete a schedule by ID
    public Mono<ServerResponse> deleteSchedule(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));

        return errorHandler(
                scheduleService.deleteSchedule(id)
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response)));
    }

    // Generic error handler
    private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response) {
        return response.onErrorResume(error -> {
            if (error instanceof WebClientResponseException errorResponse) {
                if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("error", "Resource not found: " + errorResponse.getMessage());
                    body.put("timestamp", new Date());
                    body.put("status", errorResponse.getStatusCode().value());
                    return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(body);
                }
                return ServerResponse.status(errorResponse.getStatusCode())
                        .bodyValue(errorResponse.getResponseBodyAsString());
            }
            Map<String, Object> body = new HashMap<>();
            body.put("error", "An unexpected error occurred");
            body.put("timestamp", new Date());
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(body);
        });
    }
}