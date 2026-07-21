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
                .flatMap(equipament -> equipamentService.createEquipament(equipament)
                        .flatMap(created -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(created)))
                .onErrorResume(this::errorHandler);
    }

    // Handler to get all Equipaments
    public Mono<ServerResponse> getAllEquipaments(ServerRequest request) {
        return equipamentService.getAllEquipaments()
                .collectList()
                .flatMap(list -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list))
                .onErrorResume(this::errorHandler);
    }

    // Handler to get an Equipament by name
    public Mono<ServerResponse> getEquipamentByName(ServerRequest request) {
        String name = request.pathVariable("name");
        return equipamentService.getEquipamentByName(name)
                .flatMap(equipament -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(equipament))
                .onErrorResume(this::errorHandler);
    }

    // Handler to update an Equipament by id
    public Mono<ServerResponse> updateEquipament(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(Equipament.class)
                .flatMap(updatedEquipament -> equipamentService.updateEquipament(id, updatedEquipament)
                        .flatMap(updated -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(updated)))
                .onErrorResume(this::errorHandler);
    }

    // Handler to delete an Equipament by name
    public Mono<ServerResponse> deleteEquipamentByName(ServerRequest request) {
        String name = request.pathVariable("name");
        return equipamentService.deleteEquipamentByName(name)
                .flatMap(message -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(message))
                .onErrorResume(this::errorHandler);
    }

    // Generic error handler for all responses
    private Mono<ServerResponse> errorHandler(Throwable error) {
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
    }
}