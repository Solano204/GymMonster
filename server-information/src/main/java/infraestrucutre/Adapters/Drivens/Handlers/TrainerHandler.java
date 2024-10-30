package infraestrucutre.Adapters.Drivens.Handlers;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.ClassTrainerServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.ClassTrainer;
import infraestrucutre.Adapters.Drivens.Entities.DetailsClassTrainer;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.ImpServices.ClassTrainerService;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class TrainerHandler {

    private final ClassTrainerServiceInterface trainerService;

    // Create a new trainer
    public Mono<ServerResponse> getAllClassTrainers(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));

        return errorHandler(
                trainerService.getAllPerTrainersAllInformation(page, size)
                        .collectList()
                        .flatMap(trainers -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(trainers))
                        .switchIfEmpty(ServerResponse.notFound().build()));
    }

    public Mono<ServerResponse> getAllSpecialtyOfTrainer(ServerRequest request) {
        String username = request.pathVariable("username");

        return errorHandler(
                trainerService.getAllSpecialtyOfTrainer(username)
                        .collectList()
                        .flatMap(specialties -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(specialties))
                        .switchIfEmpty(ServerResponse.notFound().build()));
    }



    public Mono<ServerResponse> deleteClassTrainerByUsername(ServerRequest request) {
        String username = request.pathVariable("username");

        return errorHandler(
                trainerService.deleteClassTrainerUsername(username)
                        .then(ServerResponse.ok()
                                .bodyValue("Trainer deleted successfully")));
    }

    public Mono<ServerResponse> getWorkClassesByTrainer(ServerRequest request) {
        String username = request.pathVariable("username");

        return errorHandler(
                trainerService.getWorkClassesByTrainer(username)
                        .collectList()
                        .flatMap(workClasses -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(workClasses))
                        .switchIfEmpty(ServerResponse.notFound().build()));
    }

    public Mono<ServerResponse> deleteClassTrainerByUsernameWithPassword(ServerRequest request) {
        String username = request.pathVariable("username");
        String password = request.pathVariable("password");

        return errorHandler(
                trainerService.deleteClassTrainerUsername(username, password)
                        .flatMap(result -> ServerResponse.ok()
                                .contentType(MediaType.TEXT_PLAIN)
                                .bodyValue(result)));
    }

    public Mono<ServerResponse> createPerTrainer(ServerRequest request) {
        return request.bodyToMono(AllTrainer.class)
                .flatMap(trainer -> errorHandler(
                        trainerService.createPerTrainer(trainer)
                                .flatMap(errors -> {
                                    if (!errors.isEmpty()) {
                                        return ServerResponse.badRequest().bodyValue(errors);
                                    }
                                    return ServerResponse.ok().bodyValue("Trainer created successfully.");
                                })));
    }

    public Mono<ServerResponse> updateTrainerAllDetailInformation(ServerRequest request) {
        String username = request.pathVariable("username");
        return request.bodyToMono(DetailsClassTrainer.class)
            .flatMap(details -> errorHandler(
                trainerService.updateTrainerAllDetailInformation(username, details)
                    .flatMap(result -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(result))
            ));
    }

    
    public Mono<ServerResponse> updateTrainerPassword(ServerRequest request) {
        String username = request.pathVariable("username");
        String oldPassword = request.pathVariable("oldPassword");
        String newPassword = request.pathVariable("newPassword");
        return errorHandler(
            trainerService.updateTrainerPassword(username, newPassword, oldPassword)
                .flatMap(result -> ServerResponse.ok().bodyValue(result))
        );
    }
    
    public Mono<ServerResponse> updateTrainerUsername(ServerRequest request) {
        String oldUsername = request.pathVariable("oldUsername");
        String newUsername = request.pathVariable("newUsername");
        String password = request.pathVariable("password");
        return errorHandler(
            trainerService.updateTrainerUsername(oldUsername, newUsername, password)
                .flatMap(result -> ServerResponse.ok().bodyValue(result))
        );
    }
    
    public Mono<ServerResponse> updateClientEmail(ServerRequest request) {
        String username = request.pathVariable("username");
        String newEmail = request.pathVariable("newEmail");
        return errorHandler(
            trainerService.updateClientEmail(username, newEmail)
                .flatMap(result -> ServerResponse.ok().bodyValue(result))
        );
    }
    
    public Mono<ServerResponse> addSpecialtyToTrainer(ServerRequest request) {
        String username = request.pathVariable("username");
        String newSpecialty = request.pathVariable("newSpecialty");
        return errorHandler(
            trainerService.addSpecialtyToTrainer(username, newSpecialty)
                    .flatMap(result -> ServerResponse.ok().bodyValue(result))
        );
    }
    
    public Mono<ServerResponse> dessociateSpecialty(ServerRequest request) {
        String username = request.pathVariable("username");
        String specialty = request.pathVariable("specialty");
        return errorHandler(
            trainerService.dessociateSpecialty(username, specialty)
                .flatMap(result -> ServerResponse.ok().bodyValue(result))
        );
    }
    
    public Mono<ServerResponse> addClassToTrainer(ServerRequest request) {
        String username = request.pathVariable("username");
        String newClass = request.pathVariable("newClass");
        return errorHandler(
            trainerService.addClassToTrainer(username, newClass)
                .flatMap(result -> ServerResponse.ok().bodyValue(result))
        );
    }
    
    public Mono<ServerResponse> dessociateClassToTrainer(ServerRequest request) {
        String username = request.pathVariable("username");
        String className = request.pathVariable("className");
        return errorHandler(
            trainerService.dessociateClassToTrainer(username, className)
                .flatMap(result -> ServerResponse.ok().bodyValue(result))
        );
    }
    // Error handler (consistent with other handler classes)
    private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response) {
        return response.onErrorResume(error -> {
            if (error instanceof WebClientResponseException errorResponse) {
                if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("error", "Trainer not found: ".concat(errorResponse.getMessage()));
                    body.put("timestamp", LocalDate.now());
                    body.put("status", errorResponse.getStatusCode().value());
                    return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(body);
                }
                return ServerResponse.status(errorResponse.getStatusCode())
                        .bodyValue(errorResponse.getResponseBodyAsString());
            }
            Map<String, Object> body = new HashMap<>();
            body.put("error", "An unexpected error occurred");
            body.put("timestamp", LocalDate.now());
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(body);
        });
    }
}
