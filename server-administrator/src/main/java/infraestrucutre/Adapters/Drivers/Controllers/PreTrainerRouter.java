package infraestrucutre.Adapters.Drivers.Controllers;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.PreTrainerHandler;
import reactor.core.publisher.Mono;

// Define the routes
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class PreTrainerRouter {

    @Bean
public RouterFunction<ServerResponse> routeTrainer(PreTrainerHandler handler) {
    return RouterFunctions
        .route()
        .POST("/api/admin/trainer", handler::createPerTrainer) // Route to create a new trainer
        .GET("/api/admin/allTrainer", handler::getAllTrainers) // Route to get all trainers
        .DELETE("/api/admin/trainer/{username}/{password}", handler::deletePerTrainer) // Route to delete a trainer
        .GET("/api/admin/trainer/{username}/allClients", handler::getAllClientsFromTrainer) // Route to get all clients from a specific trainer
        .GET("/api/admin/trainer/{username}/specialties", handler::getAllSpecialtiesFromTrainer) // Route to get all specialties from a specific trainer
        .PUT("/api/admin/trainer/{username}/password/{newPassword}/{oldPassword}", handler::updateTrainerPassword) // Route to update trainer password
        .PUT("/api/admin/trainer/{username}/email/{newEmail}", handler::updateEmail) // Route to update trainer email
        .PUT("/api/admin/trainer/{oldUsername}/username/{newUsername}", handler::updateTrainerUsername) // Route to update trainer username
        .PUT("/api/admin/trainer/{username}/BasicInformation", handler::updateTrainerDetails) // Route to update trainer details
        .POST("/api/admin/trainer/{username}/addSpecialty/{newSpecialty}", handler::addSpecialtyToTrainer) // Route to add specialty to trainer
        .DELETE("/api/admin/trainer/{username}/removeSpecialty/{specialty}", handler::removeSpecialtyFromTrainer) // Route to remove specialty from trainer
        .build();
}

}

    

