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
        return workClassService.getAllWorkClasses()
                .collectList()
                .flatMap(list -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list))
                .onErrorResume(this::errorHandler);
    }

    // Get schedules for a specific work class
    public Mono<ServerResponse> getWorkClassSchedules(ServerRequest request) {
        String name = request.pathVariable("name");

        return workClassService.getCSchedulesByWorkClassWithPagination(name)
                .collectList()
                .flatMap(list -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list))
                .onErrorResume(this::errorHandler);
    }

    // Get clients by work class with pagination
    public Mono<ServerResponse> getClientsByWorkClass(ServerRequest request) {
        String name = request.pathVariable("name");
        int page = Integer.parseInt(request.queryParam("page").orElse("0")); // Default to page 0
        int size = Integer.parseInt(request.queryParam("size").orElse("10")); // Default size 10

        return workClassService.getClientsByWorkClassWithPagination(name, page, size)
                .collectList()
                .flatMap(list -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list))
                .onErrorResume(this::errorHandler);
    }

    // Get trainers by work class with pagination
    public Mono<ServerResponse> getTrainersByWorkClass(ServerRequest request) {
        String name = request.pathVariable("name");
        int page = Integer.parseInt(request.queryParam("page").orElse("0")); // Default to page 0
        int size = Integer.parseInt(request.queryParam("size").orElse("10")); // Default size 10

        return workClassService.getTrainersByWorkClassWithPagination(name, page, size)
                .collectList()
                .flatMap(list -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list))
                .onErrorResume(this::errorHandler);
    }

    // Create a new work class
    public Mono<ServerResponse> createWorkClass(ServerRequest request) {
        return request.bodyToMono(WorkClass.class)
                .flatMap(workClass -> workClassService.createWorkclass(workClass)
                        .flatMap(created -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(created)))
                .onErrorResume(this::errorHandler);
    }

    // Update a specific work class
    public Mono<ServerResponse> updateWorkClass(ServerRequest request) {
        String name = request.pathVariable("name");
        return request.bodyToMono(WorkClass.class)
                .flatMap(workClass -> workClassService.updateWorkClass(workClass, name)
                        .flatMap(updated -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(updated)))
                .onErrorResume(this::errorHandler);
    }

    // Delete a work class by name
    public Mono<ServerResponse> deleteWorkClass(ServerRequest request) {
        String name = request.pathVariable("name");

        return workClassService.deleteWorkClass(name)
                .flatMap(message -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(message))
                .onErrorResume(this::errorHandler);
    }

    // Error handler method
    private Mono<ServerResponse> errorHandler(Throwable error) {
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
    }
}
