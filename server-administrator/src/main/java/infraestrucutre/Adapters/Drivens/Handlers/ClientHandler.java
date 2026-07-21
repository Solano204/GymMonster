package infraestrucutre.Adapters.Drivens.Handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.ClientServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoChangePassword;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDeleteAccount;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Mono;
import java.util.*;
import reactor.core.publisher.Flux;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Data
public class ClientHandler {

    private final ClientServiceInterface clientService;


    // Handler to get all clients
    public Mono<ServerResponse> getAllClients(ServerRequest request) {
        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(clientService.getAllClients(), AllClient.class));
    }

    public Mono<ServerResponse> getAllClass(ServerRequest request) {
        String username = request.pathVariable("username");

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(clientService.getWorkClassesByClientId(username), AllClient.class));
    }

    // Handler to create a new client
    public Mono<ServerResponse> createClient(ServerRequest request) {
        return request.bodyToMono(AllClient.class)
                .flatMap(client -> clientService.createClient(client)
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response))
                        .onErrorResume(error -> errorHandler(Mono.error(error))));
    }

    // Handler to update client information
    public Mono<ServerResponse> updateClient(ServerRequest request) {
        String username = request.pathVariable("username");

        return request.bodyToMono(DtoDetailUserSent.class)
                .flatMap(client -> clientService.updateAllInformation(client, username)
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response))
                        .onErrorResume(error -> errorHandler(Mono.error(error))));
    }

    // Handler to delete a client
    // Was reading a "password" path variable that the route (ClientRouter)
    // never actually declares - request.pathVariable("password") threw
    // IllegalArgumentException on every single call, so this endpoint was
    // completely broken. Reading the password from the JSON body fixes both
    // that crash and the credentials-in-URL issue at once (same reasoning
    // and DTO shape as web-page's already-fixed RouterRegister).
    public Mono<ServerResponse> deleteClient(ServerRequest request) {
        String username = request.pathVariable("username");

        return errorHandler(
                request.bodyToMono(DtoDeleteAccount.class)
                        .flatMap(body -> clientService.deleteClient(username, body.password())
                                .flatMap(response -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(response))));
    }

    // Handler to validate if a username exists
    public Mono<ServerResponse> validateIfUserNameExists(ServerRequest request) {
        String username = request.pathVariable("username");

        return errorHandler(
                clientService.validateIfUserNameExistsClient(username)
                        .flatMap(exists -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(exists)));
    }

    // Handler to validate if an email exists
    public Mono<ServerResponse> validateIfEmailExists(ServerRequest request) {
        String email = request.pathVariable("email");

        return errorHandler(
                clientService.validateIfEmailExistsClient(email)
                        .flatMap(exists -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(exists)));
    }

    // Handler to change password - reads credentials from the JSON body, not
    // path segments (same fix as web-page's RouterRegister/deleteClient above).
    public Mono<ServerResponse> changePassword(ServerRequest request) {
        String username = request.pathVariable("username");

        return errorHandler(
                request.bodyToMono(DtoChangePassword.class)
                        .flatMap(body -> clientService
                                .changePassword(username, body.oldPassword(), body.newPassword())
                                .flatMap(response -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(response))));
    }

    // Handler to change email
    public Mono<ServerResponse> changeEmail(ServerRequest request) {
        String username = request.pathVariable("username");
        String email = request.pathVariable("email");

        return errorHandler(
                clientService.changeEmail(username, email)
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response)));
    }

    // Handler to change username
    public Mono<ServerResponse> changeUsername(ServerRequest request) {
        String username = request.pathVariable("username");
        String newUsername = request.pathVariable("newUsername");

        return errorHandler(
                clientService.changeUsername(username, newUsername)
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response)));
    }

    // Handler to update all information
    public Mono<ServerResponse> updateAllInformation(ServerRequest request) {
        String username = request.pathVariable("username");

        return request.bodyToMono(DtoDetailUserSent.class)
                .flatMap(newInformation -> clientService.updateAllInformation(newInformation, username)
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response))
                        .onErrorResume(error -> errorHandler(Mono.error(error))));
    }

    // Handler to validate password during registration
    public Mono<ServerResponse> validatePasswordRegister(ServerRequest request) {
        String password = request.pathVariable("password");

        return errorHandler(
                clientService.validatePasswordRegister(password)
                        .flatMap(isValid -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(isValid)));
    }

    // Handler to change membership
    public Mono<ServerResponse> changeMembership(ServerRequest request) {
        String username = request.pathVariable("username");
        String membershipType = request.pathVariable("membershipType");

        return errorHandler(
                clientService.changeMembership(username, membershipType)
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response)));
    }

    // Handler to change trainer
    public Mono<ServerResponse> changeTrainer(ServerRequest request) {
        String username = request.pathVariable("username");
        String usernameTrainer = request.pathVariable("usernameTrainer");

        return errorHandler(
                clientService.changeTrainer(username, usernameTrainer)
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response)));
    }
    // Handler to change membership
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
