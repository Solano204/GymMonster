package infraestrucutre.Adapters.Drivens.Handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.MembershipServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import infraestrucutre.Adapters.Drivens.ImpServices.MembershipService;
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
public class MembershipHandler {

    private final MembershipServiceInterface membershipService;

  
    // Handler to get all memberships
    public Mono<ServerResponse> getAllMemberships(ServerRequest request) {
        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(membershipService.getAllMemberships(), DtoMembershipReciving.class));
    }

    // Handler to create a new membership
    public Mono<ServerResponse> createMembership(ServerRequest request) {
        return request.bodyToMono(DtoMembershipReciving.class)
                .flatMap(membership -> membershipService.createMembership(membership)
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response))
                        .onErrorResume(error -> errorHandler(Mono.error(error))));
    }

    // Handler to update an existing membership
    public Mono<ServerResponse> updateMembership(ServerRequest request) {
        String id = request.pathVariable("id"); // Assuming 'id' is the path variable
        return request.bodyToMono(DtoMembershipReciving.class)
                .flatMap(updatedMembership -> membershipService.updateMembership(id, updatedMembership)
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response))
                        .onErrorResume(error -> errorHandler(Mono.error(error))));
    }

    // Handler to delete a membership by type
    public Mono<ServerResponse> deleteMembershipByType(ServerRequest request) {
        String type = request.pathVariable("type"); // Assuming 'type' is the path variable
        return errorHandler(
                membershipService.deleteMembershipByType(type)
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response))
                        .onErrorResume(error -> errorHandler(Mono.error(error))));
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

