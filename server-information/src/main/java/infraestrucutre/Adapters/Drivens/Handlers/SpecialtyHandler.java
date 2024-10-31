package infraestrucutre.Adapters.Drivens.Handlers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.SpecialtyServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.Membership;
import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.Entities.Specialty;
import infraestrucutre.Adapters.Drivens.ImpServices.SpecialtyService;
import lombok.AllArgsConstructor;
import lombok.*;
import reactor.core.publisher.Mono;

import java.util.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class SpecialtyHandler {

    private final SpecialtyServiceInterface specialtyService;

    // Create a new Specialty
    public Mono<ServerResponse> createSpecialty(ServerRequest request) {
        Mono<Specialty> specialtyMono = request.bodyToMono(Specialty.class);
        return errorHandler(
            specialtyMono.flatMap(specialty -> specialtyService.createSpecialty(specialty)
                .flatMap(savedSpecialty -> ServerResponse.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(savedSpecialty)))
        );
    }

    // Get all Specialties
    public Mono<ServerResponse> getAllSpecialties(ServerRequest request) {
        return errorHandler(
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(specialtyService.getAllSpecialties(), Specialty.class)
        );
    }

    // Get a Specialty by ID
    public Mono<ServerResponse> getSpecialtyById(ServerRequest request) {
        Integer id = Integer.parseInt(request.pathVariable("id")); // Corrected to ID
        return errorHandler(
            specialtyService.getSpecialtyById(id) // Ensure the service method matches (get by ID)
                .flatMap(specialty -> ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(specialty))
                .switchIfEmpty(ServerResponse.notFound().build())
        );
    }

    public Mono<ServerResponse> getSpecialtyByName(ServerRequest request) {
        String name = request.pathVariable("name");
        return errorHandler(
            specialtyService.getSpecialtyByName(name) // Ensure the service method matches (get by ID)
                .flatMap(specialty -> ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(specialty))
                .switchIfEmpty(ServerResponse.notFound().build())
        );
    }


    // Update a Specialty by ID
    public Mono<ServerResponse> updateSpecialty(ServerRequest request) {
        String name = request.pathVariable("name");
        Mono<Specialty> specialtyMono = request.bodyToMono(Specialty.class);
        return errorHandler(
            specialtyMono.flatMap(specialty -> specialtyService.updateSpecialtyName(name, specialty))
                .flatMap(updatedSpecialty -> ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updatedSpecialty))
                .switchIfEmpty(ServerResponse.notFound().build())
        );
    }

    // Delete a Specialty by ID
    public Mono<ServerResponse> deleteSpecialtyName(ServerRequest request) {
        String name = request.pathVariable("name");
        return errorHandler(
            specialtyService.deleteSpecialtyByName(name)
                .then(ServerResponse.noContent().build())
        );
    }

    // Error handler
    private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response) {
        return response.onErrorResume(error -> {
            if (error instanceof WebClientResponseException errorResponse) {
                if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("error", "Specialty not found: ".concat(errorResponse.getMessage()));
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
