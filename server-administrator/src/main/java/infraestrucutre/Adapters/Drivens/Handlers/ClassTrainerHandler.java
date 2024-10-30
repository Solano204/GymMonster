package infraestrucutre.Adapters.Drivens.Handlers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.DetailsUser;
import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.ImpServices.ClassTrainerService;
import infraestrucutre.Adapters.Drivens.ImpServices.MembershipService;
import infraestrucutre.Adapters.Drivens.ImpServices.PoolService;
import infraestrucutre.Adapters.Drivens.ImpServices.PromotionService;
import lombok.*;
import reactor.core.publisher.Mono;
import java.util.*; 

@Component
@Data
public class ClassTrainerHandler {

    private final ClassTrainerService classTrainerService;



    // Handler to create a new trainer
    public Mono<ServerResponse> createClassTrainer(ServerRequest request) {
        return request.bodyToMono(AllTrainer.class)
                .flatMap(trainer -> errorHandler(
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(classTrainerService.createClassTrainer(trainer), String.class)
                ));
    }

    // Handler to get all trainers with pagination
    public Mono<ServerResponse> getAllTrainers(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0")); // Default to page 0
        int size = Integer.parseInt(request.queryParam("size").orElse("10")); // Default size 10

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(classTrainerService.getAllTrainers(page, size), DtoTrainerData.class)
        );
    }

    // Handler to get all specialties of a specific trainer
    public Mono<ServerResponse> getAllSpecialtiesFromTrainer(ServerRequest request) {
        String username = request.pathVariable("username");

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(classTrainerService.getAllSpecialtiesFromTrainer(username), DtoSpecialtyRecived.class)
        );
    }

    // Handler to update trainer's detailed information
    public Mono<ServerResponse> updateTrainerAllDetailInformation(ServerRequest request) {
        String username = request.pathVariable("username");
        return request.bodyToMono(DtoDetailUserReciving.class)
                .flatMap(details -> errorHandler(
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(classTrainerService.updateTrainerAllDetailInformation(username, details), String.class)
                ));
    }

    // Handler to get work classes by trainer
    public Mono<ServerResponse> getWorkClassesByTrainer(ServerRequest request) {
        String username = request.pathVariable("username");

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(classTrainerService.getWorkClassesByTrainer(username), WorkClass.class)
        );
    }

    // Handler to update trainer's password
    public Mono<ServerResponse> updateTrainerPassword(ServerRequest request) {
        String username = request.pathVariable("username");
        String oldPassword = request.pathVariable("oldPassword");
        String newPassword = request.pathVariable("newPassword");

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(classTrainerService.updateTrainerPassword(username, oldPassword, newPassword), String.class)
        );
    }

    // Handler to update trainer's username
    public Mono<ServerResponse> updateTrainerUsername(ServerRequest request) {
        String oldUsername = request.pathVariable("oldUsername");
        String newUsername = request.pathVariable("newUsername");

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(classTrainerService.updateTrainerUsername(oldUsername, newUsername), String.class)
        );
    }

    // Handler to update trainer's email
    public Mono<ServerResponse> updateTrainerEmail(ServerRequest request) {
        String username = request.pathVariable("username");
        String newEmail = request.pathVariable("newEmail");

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(classTrainerService.updateEmail(username, newEmail), String.class)
        );
    }

    // Handler to add a specialty to a trainer
    public Mono<ServerResponse> addSpecialtyToTrainer(ServerRequest request) {
        String username = request.pathVariable("username");
        String newSpecialty = request.pathVariable("newSpecialty");

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(classTrainerService.addSpecialtyToTrainer(username, newSpecialty), String.class)
        );
    }

    // Handler to dissociate a specialty from a trainer
    public Mono<ServerResponse> dessociateSpecialty(ServerRequest request) {
        String username = request.pathVariable("username");
        String specialty = request.pathVariable("specialty");

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(classTrainerService.dessociateSpecialty(username, specialty), String.class)
        );
    }

    // Handler to add a class to a trainer
    public Mono<ServerResponse> addClassToTrainer(ServerRequest request) {
        String username = request.pathVariable("username");
        String newClass = request.pathVariable("newClass");

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(classTrainerService.addClassToTrainer(username, newClass), String.class)
        );
    }

    // Handler to dissociate a class from a trainer
    public Mono<ServerResponse> dessociateClassToTrainer(ServerRequest request) {
        String username = request.pathVariable("username");
        String className = request.pathVariable("className");

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(classTrainerService.dessociateClassToTrainer(username, className), String.class)
        );
    }

    // Handler to delete a trainer by username with password
    public Mono<ServerResponse> deleteClassTrainerByUsernameWithPassword(ServerRequest request) {
        String username = request.pathVariable("username");
        String password = request.pathVariable("password");

        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(classTrainerService.deleteClassTrainerByUsernameWithPassword(username, password), String.class)
        );
    }

    // Error handler method
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
