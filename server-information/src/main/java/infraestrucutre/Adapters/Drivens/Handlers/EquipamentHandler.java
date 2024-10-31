package infraestrucutre.Adapters.Drivens.Handlers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.EquipamentServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.Equipament;
import infraestrucutre.Adapters.Drivens.Entities.Membership;
import infraestrucutre.Adapters.Drivens.ImpServices.EquipamentService;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Mono;

import java.util.*;
@Component
@AllArgsConstructor
public class EquipamentHandler {

    private final EquipamentServiceInterface equipamentService;

    // Create a new Equipament
    public Mono<ServerResponse> createEquipament(ServerRequest request) {
        Mono<Equipament> equipamentMono = request.bodyToMono(Equipament.class);
        return errorHandler(
                equipamentMono.flatMap(equipament -> equipamentService.createEquipament(equipament)
                        .flatMap(savedEquipament -> ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(savedEquipament)))
        );
    }

    // Get all Equipaments
    public Mono<ServerResponse> getAllEquipaments(ServerRequest request) {
        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(equipamentService.getAllEquipaments(), Equipament.class)
        );
    }

    // Get an Equipament by name (ID)
    public Mono<ServerResponse> getEquipamentByName(ServerRequest request) {
        String name = request.pathVariable("name");
        return errorHandler(
                equipamentService.getEquipamentByName(name)
                        .flatMap(equipament -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(equipament))
                        .switchIfEmpty(ServerResponse.notFound().build())
        );
    }

    // Update an Equipament by ID
    public Mono<ServerResponse> updateEquipament(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        Mono<Equipament> equipamentMono = request.bodyToMono(Equipament.class);
        return errorHandler(
                equipamentMono.flatMap(equipament -> equipamentService.updateEquipament(id, equipament))
                        .flatMap(updatedEquipament -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(updatedEquipament))
                        .switchIfEmpty(ServerResponse.notFound().build())
        );
    }

    // Delete an Equipament by name
    public Mono<ServerResponse> deleteEquipamentByName(ServerRequest request) {
        String name = request.pathVariable("name");
        return errorHandler(
                equipamentService.deleteEquipament(name)
                        .then(ServerResponse.noContent().build())
        );
    }

    // Error handler
    private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response) {
        return response.onErrorResume(error -> {
            if (error instanceof WebClientResponseException errorResponse) {
                if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("error", "Equipament not found: ".concat(errorResponse.getMessage()));
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
