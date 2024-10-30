package infraestrucutre.Adapters.Drivens.Handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.TrainerInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoPoolReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerReciving;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.ImpServices.MembershipService;
import infraestrucutre.Adapters.Drivens.ImpServices.PoolService;
import infraestrucutre.Adapters.Drivens.ImpServices.RegisterService;
import infraestrucutre.Adapters.Drivens.ImpServices.TrainerService;
import lombok.*;
import reactor.core.publisher.Mono;
import java.util.*; 
@Component
@AllArgsConstructor
public class TrainerHandler {

    private final TrainerInterface trainerService;

    // Get all trainers with pagination
    public Mono<ServerResponse> getAllTrainers(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0")); // Default to page 0
        int size = Integer.parseInt(request.queryParam("size").orElse("10")); // Default size 10

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(trainerService.getAllPerTrainers(page, size), DtoTrainerReciving.class));
    }

    // Get all specialties for a specific trainer
    public Mono<ServerResponse> getAllSpecialtiesFromTrainer(ServerRequest request) {
        String username = request.pathVariable("username");

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(trainerService.getAllSpecialtiesFromTrainer(username), DtoSpecialtyRecived.class));
    }

    // Error handler method
    private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response) {
        return response.onErrorResume(error -> {
            if (error instanceof WebClientResponseException errorResponse) {
                if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("error", "Trainer or specialties not found: " + errorResponse.getMessage());
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
