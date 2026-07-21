package infraestrucutre.Adapters.Drivers.Controllers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.PerTrainerHandler;



@Configuration
public class RoutersPreTrainer {
    @Bean
    public RouterFunction<ServerResponse> perTrainersRoutes(PerTrainerHandler perTrainerHandler) {
        // Credentials moved off the URL and into the JSON body throughout (password/
        // newPassword/oldPassword were path segments - leak into access logs, browser
        // history, and Referer headers; same fix already applied and tested in
        // web-page's RouterRegister/RouterRegisterTest).
        //
        // Also dropped the old PUT("/api/pertrainers/AD/{username}/{password}") route
        // that used to point at updateTrainerPassword too: that handler reads
        // newPassword/oldPassword path variables, which a 2-segment
        // {username}/{password} route never supplied - matching it would have thrown
        // immediately. It was a dead/broken duplicate of the updatePassword route
        // below, not a second real endpoint.
        return route(POST("/api/pertrainers/AD/create"), perTrainerHandler::createPerTrainer) // Create a new PerTrainer
                .andRoute(GET("/api/pertrainers/allInformation"), perTrainerHandler::getAllPerTrainersAllInformation) // Get all PerTrainers with all information
                .andRoute(DELETE("/api/pertrainers/AD/{username}/delete"), perTrainerHandler::deletePerTrainer) // Delete PerTrainer by username
                .andRoute(GET("/api/pertrainers/AllClients/{username}"), perTrainerHandler::getAllClients) // Get all clients for a PerTrainer
                .andRoute(GET("/api/pertrainers/{username}/specialties"), perTrainerHandler::getAllSpecialties) // Get all specialties for a PerTrainer
                .andRoute(PUT("/api/pertrainers/{username}/AD/updateBasicInformation"), perTrainerHandler::updateTrainerDetails) // Update trainer details
                .andRoute(PUT("/api/pertrainers/AD/{username}/updatePassword"), perTrainerHandler::updateTrainerPassword) // Update trainer password
                .andRoute(PUT("/api/pertrainers/AD/{username}/{newEmail}/updateEmail"), perTrainerHandler::updateEmail) // Update trainer password
                .andRoute(PUT("/api/pertrainers/AD/{oldUsername}/{newUsername}/updateUsername"), perTrainerHandler::updateTrainerUsername)
                .andRoute(POST("/api/pertrainers/AD/{username}/{newSpecialty}/assignSpecialty"), perTrainerHandler::addSpecialty) // Add specialty to trainer
                .andRoute(DELETE("/api/pertrainers/AD/{username}/{newSpecialty}/dessingSpecialty"), perTrainerHandler::removeSpecialty  ); // Remove specialty from trainer
    }
        
}
