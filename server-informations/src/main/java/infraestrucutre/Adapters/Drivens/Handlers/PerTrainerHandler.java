package infraestrucutre.Adapters.Drivens.Handlers;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.PerTrainerServiceInterface;
import ch.qos.logback.core.subst.Token;
import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoTrainer;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.DetailPerTrainer;
import infraestrucutre.Adapters.Drivens.Entities.PerTrainer;
import infraestrucutre.Adapters.Drivens.ImpServices.PerTrainerService;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Mono;
@Component
@AllArgsConstructor
@Data
public class PerTrainerHandler {

    private final PerTrainerServiceInterface perTrainerService;

    // Create a new PerTrainer
    // Handler for creating a new trainer
    public Mono<ServerResponse> createPerTrainer(ServerRequest request) {
        return request.bodyToMono(AllTrainer.class)
                .flatMap(trainer -> perTrainerService.createPerTrainer(trainer)
                        .flatMap(errors -> {
                            if (errors.isEmpty()) {
                                return ServerResponse.ok().bodyValue("Trainer successfully created.");
                            } else {
                                return ServerResponse.badRequest().bodyValue(errors);
                            }
                        })
                );
    }

    // Handler to fetch a list of all trainers with pagination
    public Mono<ServerResponse> getAllPerTrainers(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));

        return perTrainerService.getAllPerTrainers(page, size)
                .collectList()
                .flatMap(trainers -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(trainers))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    // Handler to fetch all trainer information with pagination
    public Mono<ServerResponse> getAllPerTrainersAllInformation(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));

        return perTrainerService.getAllPerTrainersAllInformation(page, size)
                .collectList()
                .flatMap(trainers -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(trainers))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    // Handler to fetch a specific trainer by ID
    public Mono<ServerResponse> getPerTrainerById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));

        return perTrainerService.getPerTrainerById(id)
                .flatMap(trainer -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(trainer))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    // Handler to delete a trainer by username and password
    public Mono<ServerResponse> deletePerTrainer(ServerRequest request) {
        String username = request.pathVariable("username");
        String password = request.pathVariable("password");

        return perTrainerService.deletePerTrainerUsername(username, password)
                .flatMap(result -> ServerResponse.ok().bodyValue(result))
                .switchIfEmpty(ServerResponse.notFound().build());
    }


     // Get all specialties by username
    public Mono<ServerResponse> getAllSpecialties(ServerRequest request) {
        String username = request.pathVariable("username");

        return errorHandler(
                perTrainerService.getAllSpecialties(username)
                        .collectList() // Collect the results into a list
                        .flatMap(specialties -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(specialties))
                        .switchIfEmpty(ServerResponse.notFound().build())
        );
    }
    public Mono<ServerResponse> getAllClients(ServerRequest request) {
        String username = request.pathVariable("username");
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        return errorHandler(
                perTrainerService.getAllClients(username,page,size)
                        .collectList() // Collect the results into a list
                        .flatMap(specialties -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(specialties))
                        .switchIfEmpty(ServerResponse.notFound().build())
        );
    }

    // Update trainer details by username
    public Mono<ServerResponse> updateTrainerDetails(ServerRequest request) {
        String username = request.pathVariable("username");
        Mono<DetailPerTrainer> newDetailUser = request.bodyToMono(DetailPerTrainer.class);

        return newDetailUser.flatMap(newData -> errorHandler(
                perTrainerService.updateTrainerAllDetailInformation(username, newData)
                        .flatMap(result -> ServerResponse.ok().bodyValue(result))
        ));
    }

    // Delete trainer by username and password
    public Mono<ServerResponse> deleteTrainerByUsername(ServerRequest request) {
        String username = request.pathVariable("username");
        String password = request.pathVariable("password");

        return errorHandler(
                perTrainerService.deletePerTrainerUsername(username, password)
                        .flatMap(result -> ServerResponse.ok().bodyValue(result))
        );
    }

    // Update trainer password
    public Mono<ServerResponse> updateTrainerPassword(ServerRequest request) {
        String username = request.pathVariable("username");
        String newPassword = request.pathVariable("newPassword");
        String oldPassword = request.pathVariable("oldPassword");

        return errorHandler(
                perTrainerService.updateTrainerPassword(username, newPassword, oldPassword)
                        .flatMap(result -> ServerResponse.ok().bodyValue(result))
        );
    }


    public Mono<ServerResponse> updateTrainerUsername(ServerRequest request) {
        String oldUsername = request.pathVariable("oldUsername");
        String newUsername = request.pathVariable("newUsername");
        String password = request.pathVariable("password");
        return errorHandler(
            perTrainerService.updateTrainerUsername(oldUsername, newUsername, password)
                .flatMap(result -> ServerResponse.ok().bodyValue(result))
        );
    }
    
    public Mono<ServerResponse> updateEmail(ServerRequest request) {
        String username = request.pathVariable("username");
        String newEmail = request.pathVariable("newEmail");
        return errorHandler(
            perTrainerService.updateClientEmail(username, newEmail)
                .flatMap(result -> ServerResponse.ok().bodyValue(result))
        );
    }

    // Add specialty to trainer
    public Mono<ServerResponse> addSpecialty(ServerRequest request) {
        String username = request.pathVariable("username");
        String newSpecialty = request.pathVariable("newSpecialty");

        return errorHandler(
                perTrainerService.addSpecialtyToTrainer(username, newSpecialty)
                        .flatMap(result -> ServerResponse.ok().bodyValue(result))
        );
    }

    // Remove specialty from trainer
    public Mono<ServerResponse> removeSpecialty(ServerRequest request) {
        String username = request.pathVariable("username");
        String specialty = request.pathVariable("newSpecialty");

        return errorHandler(
                perTrainerService.dessociateSpecialty(username, specialty)
                        .flatMap(result -> ServerResponse.ok().bodyValue(result))
        );
    }

    // Error handler
    private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response) {
        return response.onErrorResume(error -> {
            if (error instanceof WebClientResponseException errorResponse) {
                if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("error", "PerTrainer not found: " + errorResponse.getMessage());
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
