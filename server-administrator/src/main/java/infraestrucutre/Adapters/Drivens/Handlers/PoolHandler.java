package infraestrucutre.Adapters.Drivens.Handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.PoolServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoPoolSent;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.ImpServices.MembershipService;
import infraestrucutre.Adapters.Drivens.ImpServices.PoolService;
import infraestrucutre.Adapters.Drivens.ImpServices.PromotionService;
import lombok.*;
import reactor.core.publisher.Mono;
import java.util.*; 
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
@AllArgsConstructor
public class PoolHandler {

    private final PoolServiceInterface poolService;

    

    // Handler to get all pools
    public Mono<ServerResponse> getAllPools(ServerRequest request) {
        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(poolService.getAllPools(), DtoPoolSent.class));
    }

    // Handler to create a new pool
    public Mono<ServerResponse> createPool(ServerRequest request) {
        return request.bodyToMono(DtoPoolSent.class)
                .flatMap(pool -> poolService.createPool(pool)
                        .flatMap(response -> ServerResponse.created(request.uri()) // 201 Created
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response))
                        .onErrorResume(error -> errorHandler(Mono.error(error))));
    }

    // Handler to update an existing pool
    public Mono<ServerResponse> updatePool(ServerRequest request) {
        String name = request.pathVariable("name");

        return request.bodyToMono(DtoPoolSent.class)
                .flatMap(updatedPool -> poolService.updatePool(name, updatedPool)
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response))
                        .onErrorResume(error -> errorHandler(Mono.error(error))));
    }

    // Handler to delete a pool by name
    public Mono<ServerResponse> deletePoolByName(ServerRequest request) {
        String name = request.pathVariable("name");

        return poolService.deletePoolByName(name)
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
