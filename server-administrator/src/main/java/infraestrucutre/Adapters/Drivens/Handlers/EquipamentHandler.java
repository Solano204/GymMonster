package infraestrucutre.Adapters.Drivens.Handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.EquipamentServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.Equipament;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class EquipamentHandler {

    private final EquipamentServiceInterface equipamentService;

    // Handler to create a new Equipament
    public Mono<ServerResponse> createEquipament(ServerRequest request) {
        return request.bodyToMono(Equipament.class)
                .flatMap(equipament -> errorHandler(
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(equipamentService.createEquipament(equipament), Equipament.class)
                ));
    }

    // Handler to get all Equipaments
    public Mono<ServerResponse> getAllEquipaments(ServerRequest request) {
        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(equipamentService.getAllEquipaments(), Equipament.class)
        );
    }

    // Handler to get an Equipament by name
    public Mono<ServerResponse> getEquipamentByName(ServerRequest request) {
        String name = request.pathVariable("name");
        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(equipamentService.getEquipamentByName(name), Equipament.class)
        );
    }

    // Handler to update an Equipament by id
    public Mono<ServerResponse> updateEquipament(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(Equipament.class)
                .flatMap(updatedEquipament -> errorHandler(
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(equipamentService.updateEquipament(id, updatedEquipament), Equipament.class)
                ));
    }

    // Handler to delete an Equipament by name
    public Mono<ServerResponse> deleteEquipamentByName(ServerRequest request) {
        String name = request.pathVariable("name");
        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(equipamentService.deleteEquipamentByName(name), String.class)
        );
    }

    // Generic error handler for all responses
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