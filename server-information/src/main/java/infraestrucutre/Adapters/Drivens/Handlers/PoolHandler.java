package infraestrucutre.Adapters.Drivens.Handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;

import static org.springframework.http.MediaType.*;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.PoolServiceInterface;
import ch.qos.logback.core.subst.Token;
import infraestrucutre.Adapters.Drivens.Entities.Pool;
import infraestrucutre.Adapters.Drivens.ImpServices.PoolService;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class PoolHandler {

    private final PoolServiceInterface poolService;

    // Create a new pool
    public Mono<ServerResponse> createPool(ServerRequest request) {
        Mono<Pool> poolMono = request.bodyToMono(Pool.class);
        return errorHandler(
                poolMono.flatMap(pool -> poolService.createPool(pool)
                        .flatMap(savedPool -> ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(savedPool))));
    }

    public Mono<ServerResponse> updatePool(ServerRequest request) {
        Long id =  Long.parseLong(request.pathVariable("id"));

        Mono<Pool> poolMono = request.bodyToMono(Pool.class);
        return errorHandler(
                poolMono.flatMap(pool -> poolService.updatePoolById(id, pool)
                        .flatMap(savedPool -> ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(savedPool))));
    }

    // Get all pools
    public Mono<ServerResponse> getAllPools(ServerRequest request) {
        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(poolService.getAllPools(), Pool.class));
    }

    // Get a pool by name
    public Mono<ServerResponse> getPoolById(ServerRequest request) {
        String name = request.pathVariable("name");
        return errorHandler(
                poolService.getPoolByName(name)
                        .flatMap(pool -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(pool))
                        .switchIfEmpty(ServerResponse.notFound().build()));
    }

    // Delete a pool by name
    public Mono<ServerResponse> deletePool(ServerRequest request) {
        String name = request.pathVariable("name");
        return errorHandler(
                poolService.deletePoolByName(name)
                        .then(ServerResponse.noContent().build()));
    }

    // Error handler method
    private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response) {
        return response.onErrorResume(error -> {
            if (error instanceof WebClientResponseException errorResponse) {
                if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("error", "Resource not found: ".concat(errorResponse.getMessage()));
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