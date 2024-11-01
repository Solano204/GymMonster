package infraestrucutre.Adapters.Drivens.Handlers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.WorkClassServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.ImpServices.WorkClassService;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class WorkClassHandler {

        private final WorkClassServiceInterface workClassService;

        // Create a new WorkClass
        public Mono<ServerResponse> createWorkClass(ServerRequest request) {
                Mono<WorkClass> workClassMono = request.bodyToMono(WorkClass.class);
                return errorHandler(
                                workClassMono.flatMap(workClass -> workClassService.createWorkClass(workClass)
                                                .flatMap(savedWorkClass -> ServerResponse.status(HttpStatus.CREATED)
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .bodyValue(savedWorkClass))));
        }

        // Get all WorkClasses
        public Mono<ServerResponse> getAllWorkClasses(ServerRequest request) {
                return errorHandler(
                                ServerResponse.ok()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .body(workClassService.getAllWorkClasses(), WorkClass.class));
        }

        // Get a WorkClass by ID
        public Mono<ServerResponse> getWorkClassById(ServerRequest request) {
                Long id = Long.valueOf(request.pathVariable("id"));
                return errorHandler(
                                workClassService.getWorkClassById(id)
                                                .flatMap(workClass -> ServerResponse.ok()
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .bodyValue(workClass))
                                                .switchIfEmpty(ServerResponse.notFound().build()));
        }

        public Mono<ServerResponse> getWorkClassByName(ServerRequest request) {
                String name = request.pathVariable("name");
                return errorHandler(
                                workClassService.getWorkClassByName(name)
                                                .flatMap(workClass -> ServerResponse.ok()
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .bodyValue(workClass))
                                                .switchIfEmpty(ServerResponse.notFound().build()));
        }

        // Update a WorkClass
        public Mono<ServerResponse> updateWorkClass(ServerRequest request) {
                String name = request.pathVariable("name");
                Mono<WorkClass> workClassMono = request.bodyToMono(WorkClass.class);
                return errorHandler(
                                workClassMono.flatMap(workClass1 ->  workClassService.getWorkClassByName(name)
                                                .flatMap(workClass2 -> workClassService
                                                                .updateWorkClass(workClass2.getId(), workClass1))
                                                .flatMap(updatedWorkClass -> ServerResponse.ok()
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .bodyValue(updatedWorkClass))
                                                .switchIfEmpty(ServerResponse.notFound().build())));
        }

        // Delete a WorkClass
        public Mono<ServerResponse> deleteWorkClass(ServerRequest request) {
                String name = request.pathVariable("name");
                return errorHandler(
                                workClassService.getWorkClassByName(name)
                                                .flatMap(workClass -> workClassService
                                                                .deleteWorkClass(workClass.getId()))
                                                .then(ServerResponse.noContent().build()));
        }

        // Get Schedules for a specific WorkClass
        public Mono<ServerResponse> getWorkClassSchedules(ServerRequest request) {
                String name = request.pathVariable("name");
                return errorHandler(
                                ServerResponse.ok()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .body(workClassService.getWorkClassSchedules(name),
                                                                Schedule.class));
        }

        // Get Clients by WorkClass with pagination
        public Mono<ServerResponse> getClientsByWorkClassWithPagination(ServerRequest request) {
                String name = request.pathVariable("name");
                int page = Integer.parseInt(request.queryParam("page").orElse("0"));
                int size = Integer.parseInt(request.queryParam("size").orElse("10"));

                return errorHandler(workClassService.getWorkClassByName(name).flatMap(workClass -> workClassService
                                .getClientsByWorkClassWithPagination(name, page, size)
                                .collectList()
                                .flatMap(detailUsers -> ServerResponse.ok()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(detailUsers))
                                .switchIfEmpty(ServerResponse.notFound().build())));
        }

        // Get Trainers by WorkClass with pagination
        public Mono<ServerResponse> getTrainersByWorkClassWithPagination(ServerRequest request) {
                String name = request.pathVariable("name");
                int page = Integer.parseInt(request.queryParam("page").orElse("0"));
                int size = Integer.parseInt(request.queryParam("size").orElse("10"));

                return errorHandler(workClassService.getWorkClassByName(name)
                                .flatMap(workClass -> workClassService.getWorkClassByName(name)
                                                .flatMap(workClass1 -> workClassService
                                                                .getTrainersByWorkClassWithPagination(name, page, size)
                                                                .collectList()
                                                                .flatMap(detailUsers -> ServerResponse.ok()
                                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                                .bodyValue(detailUsers))
                                                                .switchIfEmpty(ServerResponse.notFound().build()))));
        }

        // Error handler
        private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response) {
                return response.onErrorResume(error -> {
                        if (error instanceof WebClientResponseException errorResponse) {
                                if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                                        Map<String, Object> body = new HashMap<>();
                                        body.put("error", "WorkClass not found: ".concat(errorResponse.getMessage()));
                                        body.put("timestamp", LocalDate.now());
                                        body.put("status", errorResponse.getStatusCode().value());
                                        return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(body);
                                }
                                return ServerResponse.status(errorResponse.getStatusCode())
                                                .bodyValue(errorResponse.getResponseBodyAsString());
                        }
                        Map<String, Object> body = new HashMap<>();
                        body.put("error", "An unexpected error occurred");
                        body.put("timestamp", LocalDate.now());
                        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(body);
                });
        }
}
