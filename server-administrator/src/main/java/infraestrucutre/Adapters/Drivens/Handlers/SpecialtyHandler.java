package infraestrucutre.Adapters.Drivens.Handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.SpecialtyServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.ImpServices.MembershipService;
import infraestrucutre.Adapters.Drivens.ImpServices.PoolService;
import infraestrucutre.Adapters.Drivens.ImpServices.SpecialtyService;
import lombok.*;
import reactor.core.publisher.Mono;
import java.util.*; 
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class SpecialtyHandler {

    private final SpecialtyServiceInterface specialtyService;

    // Handler to get all specialties
    public Mono<ServerResponse> getAllSpecialties(ServerRequest request) {
        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(specialtyService.getAllSpecialties(), DtoSpecialtyRecived.class));
    }

    // Handler to get all trainers
    public Mono<ServerResponse> getAllTrainers(ServerRequest request) {
        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(specialtyService.getAllTrainers(), DtoSpecialtyRecived.class));
    }

    // Handler to create a specialty
    public Mono<ServerResponse> createSpecialty(ServerRequest request) {
        return request.bodyToMono(DtoSpecialtyRecived.class)
                .flatMap(specialty -> specialtyService.createSpecialty(specialty)
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response))
                        .onErrorResume(error -> errorHandler(Mono.error(error))));
    }

    // Handler to get a specialty by name
    public Mono<ServerResponse> getSpecialtyByName(ServerRequest request) {
        String name = request.pathVariable("name");

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(specialtyService.getSpecialtyByName(name), DtoSpecialtyRecived.class));
    }

    // Handler to update a specialty
    public Mono<ServerResponse> updateSpecialty(ServerRequest request) {
        String name = request.pathVariable("name");

        return request.bodyToMono(DtoSpecialtyRecived.class)
                .flatMap(updatedSpecialty -> specialtyService.updateSpecialty(name, updatedSpecialty)
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response))
                        .onErrorResume(error -> errorHandler(Mono.error(error))));
    }

    // Handler to delete a specialty
    public Mono<ServerResponse> deleteSpecialty(ServerRequest request) {
        String name = request.pathVariable("name");

        return specialtyService.deleteSpecialty(name)
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