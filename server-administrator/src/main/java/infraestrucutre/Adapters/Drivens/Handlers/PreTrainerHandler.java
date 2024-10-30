package infraestrucutre.Adapters.Drivens.Handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.PerTrainerInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.ImpServices.MembershipService;
import infraestrucutre.Adapters.Drivens.ImpServices.PoolService;
import lombok.*;
import reactor.core.publisher.Mono;
import java.util.*; 
@Component
@AllArgsConstructor
@Data
public class PreTrainerHandler {
    

    private final PerTrainerInterface perTrainerService;
   // Handler to create a trainer
   public Mono<ServerResponse> createPerTrainer(ServerRequest request) {
    return request.bodyToMono(AllTrainer.class)
            .flatMap(trainer -> perTrainerService.createPerTrainer(trainer)
                    .flatMap(response -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response))
                    .onErrorResume(error -> errorHandler(Mono.error(error))));
}

// Handler to get all trainers with pagination
public Mono<ServerResponse> getAllTrainers(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0")); // Default to page 0
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
    return errorHandler(
            ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(perTrainerService.getAllTrainers(page, size), DtoTrainerData.class));
}

// Handler to delete a trainer
public Mono<ServerResponse> deletePerTrainer(ServerRequest request) {
    String username = request.pathVariable("username");
    String password = request.pathVariable("password");

    return errorHandler(
            ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(perTrainerService.deletePerTrainer(username, password), String.class));
}

// Handler to get all clients from a trainer with pagination
public Mono<ServerResponse> getAllClientsFromTrainer(ServerRequest request) {
    String username = request.pathVariable("username");
    int page = Integer.parseInt(request.queryParam("page").orElse("0")); // Default to page 0
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));

    return errorHandler(
            ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(perTrainerService.getAllClientsFromTrainer(username, page, size), DtoDetailUserReciving.class));
}

// Handler to get all specialties from a trainer
public Mono<ServerResponse> getAllSpecialtiesFromTrainer(ServerRequest request) {
    String username = request.pathVariable("username");

    return errorHandler(
            ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(perTrainerService.getAllSpecialtiesFromTrainer(username), DtoSpecialtyRecived.class));
}

// Handler to update trainer's password
public Mono<ServerResponse> updateTrainerPassword(ServerRequest request) {
    String username = request.pathVariable("username");
    String newPassword = request.pathVariable("newPassword");
    String oldPassword = request.pathVariable("oldPassword");

    return perTrainerService.updateTrainerPassword(username, newPassword, oldPassword)
            .flatMap(response -> ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(response))
            .onErrorResume(error -> errorHandler(Mono.error(error)));
}

// Handler to update trainer's email
public Mono<ServerResponse> updateEmail(ServerRequest request) {
    String username = request.pathVariable("username");
    String newEmail = request.pathVariable("newEmail");

    return perTrainerService.updateEmail(username, newEmail)
            .flatMap(response -> ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(response))
            .onErrorResume(error -> errorHandler(Mono.error(error)));
}

// Handler to update trainer's username
public Mono<ServerResponse> updateTrainerUsername(ServerRequest request) {
    String oldUsername = request.pathVariable("oldUsername");
    String newUsername = request.pathVariable("newUsername");

    return perTrainerService.updateTrainerUsername(oldUsername, newUsername)
            .flatMap(response -> ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(response))
            .onErrorResume(error -> errorHandler(Mono.error(error)));
}

// Handler to update trainer's details
public Mono<ServerResponse> updateTrainerDetails(ServerRequest request) {
    String username = request.pathVariable("username");

    return request.bodyToMono(DtoDetailUserReciving.class)
            .flatMap(details -> perTrainerService.updateTrainerDetails(username, details)
                    .flatMap(response -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response))
                    .onErrorResume(error -> errorHandler(Mono.error(error))));
}

// Handler to add a specialty to a trainer
public Mono<ServerResponse> addSpecialtyToTrainer(ServerRequest request) {
    String username = request.pathVariable("username");
    String newSpecialty = request.pathVariable("newSpecialty");

    return perTrainerService.addSpecialtyToTrainer(username, newSpecialty)
            .flatMap(response -> ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(response))
            .onErrorResume(error -> errorHandler(Mono.error(error)));
}

// Handler to remove a specialty from a trainer
public Mono<ServerResponse> removeSpecialtyFromTrainer(ServerRequest request) {
    String username = request.pathVariable("username");
    String specialty = request.pathVariable("specialty");

    return perTrainerService.removeSpecialtyFromTrainer(username, specialty)
            .flatMap(response -> ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(response))
            .onErrorResume(error -> errorHandler(Mono.error(error)));
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
