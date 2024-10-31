package infraestrucutre.Adapters.Drivens.Handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.ScheduleServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.Membership;
import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.ImpServices.ScheduleService;
import lombok.AllArgsConstructor;
import lombok.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
@AllArgsConstructor
public class ScheduleHandler {

    private final ScheduleServiceInterface scheduleService;

    // Create a new Schedule
    public Mono<ServerResponse> createSchedule(ServerRequest request) {
        Mono<Schedule> scheduleMono = request.bodyToMono(Schedule.class);
        return errorHandler(
                scheduleMono.flatMap(schedule -> scheduleService.createSchedule(schedule)
                        .flatMap(savedSchedule -> ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(savedSchedule))));
    }

    // Get all Schedules
    public Mono<ServerResponse> getAllSchedules(ServerRequest request) {
        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(scheduleService.getAllSchedules(), Schedule.class));
    }

    // Get a Schedule by start time
    public Mono<ServerResponse> getScheduleByStartTime(ServerRequest request) {
        String startTime = request.pathVariable("startTime");

        // Assuming the service returns a Flux<Schedule>
        return errorHandler(scheduleService.getScheduleByStartTime(startTime)
                .collectList() // Collect the Flux<Schedule> into a List
                .flatMap(schedules -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(schedules)) // Return the collected list as the response
                .switchIfEmpty(ServerResponse.notFound().build())); // Handle empty case
    }

    // Get a Schedule by day
    public Mono<ServerResponse> getScheduleByDay(ServerRequest request) {
        String day = request.pathVariable("day");
        // Assuming the service returns a Flux<Schedule>
        return 
        errorHandler(scheduleService.getScheduleByDay(day)
                .collectList() // Collect the Flux<Schedule> into a List
                .flatMap(schedules -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(schedules)) // Return the collected list as the response
                .switchIfEmpty(ServerResponse.notFound().build())); // Handle the case when no schedules are found
    }

    public Mono<ServerResponse> getScheduleByDayGym(ServerRequest request) {
        String day = request.pathVariable("day");
        // Assuming the service returns a Flux<Schedule>
        return errorHandler(scheduleService.getSchedulesByDayGym(day)
                .collectList() // Collect the Flux<Schedule> into a List
                .flatMap(schedules -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(schedules)) // Return the collected list as the response
                .switchIfEmpty(ServerResponse.notFound().build())); // Handle the case when no schedules are found
    }

    // Update a Schedule by ID
    public Mono<ServerResponse> updateSchedule(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        Mono<Schedule> scheduleMono = request.bodyToMono(Schedule.class);
        return errorHandler(
                scheduleMono.flatMap(schedule -> scheduleService.updateSchedule(id, schedule))
                        .flatMap(updatedSchedule -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(updatedSchedule))
                        .switchIfEmpty(ServerResponse.notFound().build()));
    }

    // Delete a Schedule by ID
    public Mono<ServerResponse> deleteSchedule(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        return errorHandler(
                scheduleService.deleteSchedule(id)
                        .then(ServerResponse.noContent().build()));
    }

    // Error handler
    private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response) {
        return response.onErrorResume(error -> {
            if (error instanceof WebClientResponseException errorResponse) {
                if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("error", "Schedule not found: ".concat(errorResponse.getMessage()));
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