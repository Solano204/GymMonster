package infraestrucutre.Adapters.Drivens.Handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.ImpServices.MembershipService;
import infraestrucutre.Adapters.Drivens.ImpServices.PoolService;
import infraestrucutre.Adapters.Drivens.ImpServices.WorkClassService;
import lombok.*;
import reactor.core.publisher.Mono;
import java.util.*; 
@Component
@AllArgsConstructor
public class WorkClassHandler {

    private final WorkClassService workClassService;

    // Get all work classes
    public Mono<ServerResponse> getAllWorkClasses(ServerRequest request) {
        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(workClassService.getAllWorkClasses(), WorkClass.class));
    }

    // Get schedules for a specific work class
    public Mono<ServerResponse> getWorkClassSchedules(ServerRequest request) {
        String name = request.pathVariable("name");

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(workClassService.getCSchedulesByWorkClassWithPagination(name), Schedule.class));
    }

    // Get clients by work class with pagination
    public Mono<ServerResponse> getClientsByWorkClass(ServerRequest request) {
        String name = request.pathVariable("name");
        int page = Integer.parseInt(request.queryParam("page").orElse("0")); // Default to page 0
        int size = Integer.parseInt(request.queryParam("size").orElse("10")); // Default size 10

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(workClassService.getClientsByWorkClassWithPagination(name, page, size),
                                DtoDetailUserReciving.class));
    }

    // Get trainers by work class with pagination
    public Mono<ServerResponse> getTrainersByWorkClass(ServerRequest request) {
        String name = request.pathVariable("name");
        int page = Integer.parseInt(request.queryParam("page").orElse("0")); // Default to page 0
        int size = Integer.parseInt(request.queryParam("size").orElse("10")); // Default size 10

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(workClassService.getTrainersByWorkClassWithPagination(name, page, size),
                                DtoDetailUserReciving.class));
    }

    // Create a new work class
    public Mono<ServerResponse> createWorkClass(ServerRequest request) {
        return request.bodyToMono(WorkClass.class)
                .flatMap(workClass -> errorHandler(
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(workClassService.createWorkclass(workClass), String.class)
                ));
    }

    // Update a specific work class
    public Mono<ServerResponse> updateWorkClass(ServerRequest request) {
        String name = request.pathVariable("name");
        return request.bodyToMono(WorkClass.class)
                .flatMap(workClass -> errorHandler(
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(workClassService.updateWorkClass(workClass, name), String.class)
                ));
    }

    // Delete a work class by name
    public Mono<ServerResponse> deleteWorkClass(ServerRequest request) {
        String name = request.pathVariable("name");

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(workClassService.deleteWorkClass(name), String.class));
    }

    // Error handler method
    private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response) {
        return response.onErrorResume(error -> {
            if (error instanceof WebClientResponseException errorResponse) {
                if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("error", "Work class not found: " + errorResponse.getMessage());
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
