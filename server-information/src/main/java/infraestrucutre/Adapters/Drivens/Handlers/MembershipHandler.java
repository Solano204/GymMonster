package infraestrucutre.Adapters.Drivens.Handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Entities.Membership;
import infraestrucutre.Adapters.Drivens.ImpServices.MembershipService;
import lombok.AllArgsConstructor;
import lombok.*;
import reactor.core.publisher.Mono;

import java.util.*;
@Component
@AllArgsConstructor
@Data
public class MembershipHandler {

    private final MembershipService membershipService;

    // Create a new membership
    public Mono<ServerResponse> createMembership(ServerRequest request) {
        Mono<Membership> membershipMono = request.bodyToMono(Membership.class);
        return errorHandler(
                membershipMono.flatMap(membership -> membershipService.createMembership(membership)
                        .flatMap(savedMembership -> ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(savedMembership)))
        );
    }

    // Get all memberships
    public Mono<ServerResponse> getAllMemberships(ServerRequest request) {
        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(membershipService.getAllMemberships(), Membership.class)
        );
    }

    // Get a membership by type
    public Mono<ServerResponse> getMembershipByType(ServerRequest request) {
        String type = request.pathVariable("type");
        return errorHandler(
                membershipService.getMembershipByName(type)
                        .flatMap(membership -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(membership))
                        .switchIfEmpty(ServerResponse.notFound().build())
        );
    }

    // Update a membership by ID
    public Mono<ServerResponse> updateMembership(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        Mono<Membership> membershipMono = request.bodyToMono(Membership.class);
        return errorHandler(
                membershipMono.flatMap(membership -> membershipService.updateMembership(id, membership))
                        .flatMap(updatedMembership -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(updatedMembership))
                        .switchIfEmpty(ServerResponse.notFound().build())
        );
    }

    // Delete a membership by type
    public Mono<ServerResponse>     deleteMembershipByType(ServerRequest request) {
        String type = request.pathVariable("type");
        return errorHandler(
                membershipService.deleteMembership(type)
                        .then(ServerResponse.noContent().build())
        );
    }

    // Error handler method
    private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response) {
        return response.onErrorResume(error -> {
            if (error instanceof WebClientResponseException errorResponse) {
                if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("error", "Membership not found: ".concat(errorResponse.getMessage()));
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
