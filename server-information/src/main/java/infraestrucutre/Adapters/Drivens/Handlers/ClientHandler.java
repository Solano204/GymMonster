package infraestrucutre.Adapters.Drivens.Handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.ClientServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.Client;
import infraestrucutre.Adapters.Drivens.Entities.DetailUser;
import infraestrucutre.Adapters.Drivens.ImpServices.ClientService;
import infraestrucutre.Adapters.Drivens.Mappers.Mappersito;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import java.util.*;

@Component
@AllArgsConstructor
public class ClientHandler {

    private final ClientServiceInterface clientService;

    // Handler for deleting a client
    public Mono<ServerResponse> deleteClient(ServerRequest request) {
        String username = request.pathVariable("username");
        String password = request.pathVariable("password");
        return errorHandler(clientService.deleteClient(username, password)
                .flatMap(result -> ServerResponse.ok().bodyValue(result)));
    }

    public Mono<ServerResponse> getAllClientsAD(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));

        return errorHandler(
                clientService.getAllClientsAD(page, size)
                        .collectList()
                        .flatMap(trainers -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(trainers))
                        .switchIfEmpty(ServerResponse.notFound().build()));
    }

    // Handler for getting client membership details
    public Mono<ServerResponse> getClientDetailMembershipByClientId(ServerRequest request) {
        String username = request.pathVariable("username");

        return errorHandler(clientService.getClientDetailMembershipByClientId(username)
                .flatMap(clientDetail -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(clientDetail))
                .switchIfEmpty(ServerResponse.notFound().build()));
    }

    // Handler for getting work classes by client ID
    public Mono<ServerResponse> getWorkClassesByClientId(ServerRequest request) {
        String username = request.pathVariable("username");

        return errorHandler(clientService.getWorkClassesByClientId(username)
                .collectList()
                .flatMap(workClasses -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(workClasses))
                .switchIfEmpty(ServerResponse.notFound().build()));
    }

    // Handler for checking if username exists
    public Mono<ServerResponse> existsByUsernameClient(ServerRequest request) {
        String username = request.pathVariable("username");

        return errorHandler(clientService.existsByUsernameClient(username)
                .flatMap(exists -> ServerResponse.ok().bodyValue(exists)));
    }

    // Handler for checking if email exists
    public Mono<ServerResponse> existsByEmailClient(ServerRequest request) {
        String email = request.pathVariable("email");

        return errorHandler(clientService.existsByEmailClient(email)
                .flatMap(exists -> ServerResponse.ok().bodyValue(exists)));
    }

    // Handler for creating a client
    public Mono<ServerResponse> createClient(ServerRequest request) {
        return request.bodyToMono(AllClient.class)
                .flatMap(client -> errorHandler(clientService.createClient(client)
                        .flatMap(result -> ServerResponse.ok().bodyValue(result))
                        .switchIfEmpty(ServerResponse.badRequest().bodyValue("Error during client creation"))));
    }

    // Service methods
    public Mono<ServerResponse> getClientByUsername(ServerRequest request) {
        String username = request.pathVariable("username");
        return clientService.getClientByUsername(username)
                .flatMap(client -> ServerResponse.ok().bodyValue(client))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> updateClient(ServerRequest request) {
        String username = request.pathVariable("username");
        return request.bodyToMono(DetailUser.class)
                .flatMap(client -> clientService.updateClientAllDetailInformation(username, client)
                        .flatMap(result -> ServerResponse.ok().bodyValue(result))
                        .switchIfEmpty(ServerResponse.notFound().build()));
        }


        public Mono<ServerResponse> updateClientUsername(ServerRequest request) {
            String username = request.pathVariable("username");
            String newUsername = request.pathVariable("newUsername");
            String password = request.pathVariable("password");
            return clientService.updateClientUsername(username, newUsername,password)
                            .flatMap(result -> ServerResponse.ok().bodyValue(result)
                            .switchIfEmpty(ServerResponse.badRequest().bodyValue("username update failed")));
        }

    public Mono<ServerResponse> updateClientPassword(ServerRequest request) {
        String username = request.pathVariable("username");
        String newPassword = request.pathVariable("newPassword");
        String oldPassword = request.pathVariable("oldPassword");
        return  clientService.updateClientPassword(username, newPassword,oldPassword )
                        .flatMap(result -> ServerResponse.ok().bodyValue(result)
                        .switchIfEmpty(ServerResponse.badRequest().bodyValue("Password update failed")));
    }

    public Mono<ServerResponse> updateClientEmail(ServerRequest request) {
        String username = request.pathVariable("username");
        String email = request.pathVariable("email");
        return clientService.updateClientEmail(username, email)
                        .flatMap(result -> ServerResponse.ok().bodyValue(result)
                        .switchIfEmpty(ServerResponse.badRequest().bodyValue("Email update failed")));
    }

    public Mono<ServerResponse> updateClientMembership(ServerRequest request) {
        String username = request.pathVariable("username");
        String newMembership = request.pathVariable("membershipType");
        return 
                 clientService.updateClientMembership(username, newMembership)
                        .flatMap(result -> ServerResponse.ok().bodyValue(result)
                        .switchIfEmpty(ServerResponse.badRequest().bodyValue("Membership update failed")));
    }

    public Mono<ServerResponse> updateTrainer(ServerRequest request) {
        String username = request.pathVariable("username");
        String nameTrainer = request.pathVariable("usernameTrainer");
        return clientService.updateTrainer(username,nameTrainer)
                        .flatMap(result -> ServerResponse.ok().bodyValue(result)
                        .switchIfEmpty(ServerResponse.badRequest().bodyValue("Trainer update failed")));
    }

    public Mono<ServerResponse> dessignTrainer(ServerRequest request) {
        String username = request.pathVariable("username");
        String nameTrainer = request.pathVariable("usernameTrainer");
        return clientService.dessignTrainer(username,nameTrainer)
                        .flatMap(result -> ServerResponse.ok().bodyValue(result)
                        .switchIfEmpty(ServerResponse.badRequest().bodyValue("Trainer designation failed")));
    }

    public Mono<ServerResponse> dessignMembership (ServerRequest request) {
        String username = request.pathVariable("username");
        String membership = request.pathVariable("membershipType");
        return clientService.dessignClientMembership(username,membership)
                        .flatMap(result -> ServerResponse.ok().bodyValue(result)
                        .switchIfEmpty(ServerResponse.badRequest().bodyValue("Membership designation failed")));
    }


    // Error handler method
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

