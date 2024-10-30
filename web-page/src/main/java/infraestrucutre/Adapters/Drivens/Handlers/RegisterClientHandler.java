package infraestrucutre.Adapters.Drivens.Handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.ClientServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.ImpServices.RegisterService;
import infraestrucutre.Adapters.Drivens.Mappers.Mappersito;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import java.util.*;

@Component
@Validated
@AllArgsConstructor
public class RegisterClientHandler {

    private final ClientServiceInterface clientService;

    // Create a new client
    public Mono<ServerResponse> createClient(ServerRequest request) {
        Mono<AllClient> clientMono = request.bodyToMono(AllClient.class);
        return errorHandler(
                clientMono.flatMap(client -> clientService.createClient(client)
                        .flatMap(savedClient -> ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(savedClient)))
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Client not found"))));
    }

    public Mono<ServerResponse> updateClientAllInformation(ServerRequest request) {
        String username = request.pathVariable("username");
        return errorHandler(
                request.bodyToMono(DtoDetailUserSent.class)
                        .flatMap(newData -> {
                            return clientService.updateClientAllDetailInformation(username, newData)
                                    .flatMap(responseMessage -> ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(responseMessage))
                                    .switchIfEmpty(ServerResponse.notFound().build());
                        })
                        .switchIfEmpty(ServerResponse.notFound().build()));
    }

    public Mono<ServerResponse> updateClientUsername(ServerRequest request) {
        String username = request.pathVariable("username");
        String newUsername = request.pathVariable("newUsername");
        return errorHandler(
                clientService.updateClientUsername(username, newUsername)
                        .flatMap(client -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(client))
                        .switchIfEmpty(ServerResponse.notFound().build()));
    }

    public Mono<ServerResponse> updateClientEmail(ServerRequest request) {
        String username = request.pathVariable("username");
        String newEmail = request.pathVariable("email");

        return errorHandler(
                clientService.updateClientEmail(username, newEmail)
                        .flatMap(client -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(client))
                        .switchIfEmpty(ServerResponse.notFound().build()));
    }

    public Mono<ServerResponse> updateClientMembership(ServerRequest request) {
        String username = request.pathVariable("username");
        String membership = request.pathVariable("membershipType");

        return errorHandler(
                clientService.updateClientMembership(username, membership)
                        .flatMap(client -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(client))
                        .switchIfEmpty(ServerResponse.notFound().build()));
    }
    public Mono<ServerResponse> updateClientTrainer(ServerRequest request) {
        String username = request.pathVariable("username");
        String membership = request.pathVariable("newUsernameTrainer");

        return errorHandler(
                clientService.updateClientTrainer(username, membership)
                        .flatMap(client -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(client))
                        .switchIfEmpty(ServerResponse.notFound().build()));
    }

    public Mono<ServerResponse> updateClientPassword(ServerRequest request) {
        String username = request.pathVariable("username");
        String newPassword = request.pathVariable("newPassword");
        String oldPassword = request.pathVariable("oldPassword");
        return errorHandler(
                clientService.updateClientPassword(username, newPassword, oldPassword)
                        .flatMap(client -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(client))
                        .switchIfEmpty(ServerResponse.notFound().build()));
    }

    public Mono<ServerResponse> deleteClient(ServerRequest request) {
            String username = request.pathVariable("username");
            String password = request.pathVariable("password");
            return errorHandler(
                            clientService.deleteAccount(username, password)
                                            .flatMap(client -> ServerResponse.ok()
                                                            .contentType(MediaType.APPLICATION_JSON)
                                                            .bodyValue(client))
                                            .switchIfEmpty(ServerResponse.notFound().build()));
    }
    

    public Mono<ServerResponse> removeMembership(ServerRequest request) {
            String username = request.pathVariable("username");
            String membershipType = request.pathVariable("membershipType");

            return errorHandler(
                            clientService.removeMembership(username, membershipType)
                                            .flatMap(response -> ServerResponse.ok()
                                                            .contentType(MediaType.APPLICATION_JSON)
                                                            .bodyValue(response)));
    }

    // Handler to change trainer
    public Mono<ServerResponse> removeTrainer(ServerRequest request) {
            String username = request.pathVariable("username");

            String usernameTrainer = request.pathVariable("trainerUsername");
            return errorHandler(
                            clientService.removeTrainer(username, usernameTrainer)
                                            .flatMap(response -> ServerResponse.ok()
                                                            .contentType(MediaType.APPLICATION_JSON)
                                                            .bodyValue(response)));
    }


    public Mono<ServerResponse> getAllClass(ServerRequest request) {
            String username = request.pathVariable("username");

            return errorHandler(
                            ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .body(clientService.getWorkClassesByClientId(username), AllClient.class));
    }
    public Mono<ServerResponse> getClient(ServerRequest request) {
            String username = request.pathVariable("username");

            return errorHandler(
                            ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .body(clientService.getClient(username), AllClient.class));
    }

    private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response) {
        return response.onErrorResume(error -> {
            if (error instanceof WebClientResponseException errorResponse) {
                if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("error", "Client not found: " + errorResponse.getMessage());
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
