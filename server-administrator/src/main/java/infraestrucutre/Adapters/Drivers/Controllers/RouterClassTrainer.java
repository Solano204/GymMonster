package infraestrucutre.Adapters.Drivers.Controllers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.ClassTrainerHandler;
import infraestrucutre.Adapters.Drivens.Handlers.PreTrainerHandler;

import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.RequestPredicates;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
@Configuration
public class RouterClassTrainer {

    @Bean
    public RouterFunction<ServerResponse> classTrainerRoutes(ClassTrainerHandler handler) {
        return RouterFunctions
                .nest(path("/api/admin/classTrainers"), route()
                        // Route for creating a new trainer
                        .POST("/create", handler::createClassTrainer)

                        // Route for getting all trainers with pagination
                        .GET("/all", handler::getAllTrainers)

                        // Route for getting all specialties from a specific trainer
                        .GET("/{username}/specialties", handler::getAllSpecialtiesFromTrainer)

                        // Route for updating trainer's detailed information
                        .PUT("/{username}/updateBasicInformation", handler::updateTrainerAllDetailInformation)

                        // Route for getting work classes by trainer
                        .GET("/{username}/workclasses", handler::getWorkClassesByTrainer)

                        // Route for updating trainer's password
                        .PUT("/{username}/password/{oldPassword}/{newPassword}", handler::updateTrainerPassword)

                        // Route for updating trainer's username
                        .PUT("/username/{oldUsername}/{newUsername}", handler::updateTrainerUsername)

                        // Route for updating trainer's email
                        .PUT("/email/{username}/{newEmail}", handler::updateTrainerEmail)

                        // Route for adding a specialty to a trainer
                        .PUT("/{username}/addSpecialty/{newSpecialty}", handler::addSpecialtyToTrainer)

                        // Route for dissociating a specialty from a trainer
                        .DELETE("/{username}/removeSpecialty/{specialty}", handler::dessociateSpecialty)

                        // Route for adding a class to a trainer
                        .PUT("/{username}/addClass/{newClass}", handler::addClassToTrainer)

                        // Route for dissociating a class from a trainer
                        .DELETE("/{username}/removeClass/{className}", handler::dessociateClassToTrainer)

                        // Route for deleting a trainer by username and password
                        .DELETE("/{username}/delete/{password}", handler::deleteClassTrainerByUsernameWithPassword)
                        .build());
    }
}

