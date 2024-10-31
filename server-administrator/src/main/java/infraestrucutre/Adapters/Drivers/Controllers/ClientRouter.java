package infraestrucutre.Adapters.Drivers.Controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.ClientHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties.Web.Client;

@Configuration
public class ClientRouter {

    @Bean 
public RouterFunction<ServerResponse> clientRoutes(ClientHandler clientHandler) {
    return RouterFunctions
            .route()
            .GET("/api/admin/clients", clientHandler::getAllClients) // Route to get all clients
            .GET("/api/admin/clients/{username}/allClass", clientHandler::getAllClass) // Route to get all clients
            .POST("/api/admin/clients", clientHandler::createClient) // Route to create a new client
            .PUT("/api/admin/clients/{username}", clientHandler::updateClient) // Route to update client information
            .DELETE("/api/admin/clients/{username}/deleteAccount", clientHandler::deleteClient) // Route to delete a client
            .GET("/api/admin/clients/validate/username/{username}", clientHandler::validateIfUserNameExists) // Route to validate if a username exists
            .GET("/api/admin/clients/validate/email/{email}", clientHandler::validateIfEmailExists) // Route to validate if an email exists
            .PUT("/api/admin/clients/{username}/change-password/{oldPassword}/{newPassword}", clientHandler::changePassword) // Route to change password
            .PUT("/api/admin/clients/{username}/change-email/{email}", clientHandler::changeEmail) // Route to change email
            .PUT("/api/admin/clients/{username}/change-username/{newUsername}", clientHandler::changeUsername) // Route to change username
            .PUT("/api/admin/clients/{username}/update-info", clientHandler::updateAllInformation) // Route to update all client information
            .POST("/api/admin/clients/{username}/{membershipType}/change-membership", clientHandler::changeMembership) // Route to change membership
            .POST("/api/admin/clients/{username}/{usernameTrainer}/change-trainer", clientHandler::changeTrainer) // Route to change trainer
            .DELETE("/api/admin/clients/{username}/{membershipType}/delete-membership", clientHandler::removeMembership) // Route to change membership
            .DELETE("/api/admin/clients/{username}/{trainerUsername}/remove-trainer", clientHandler::removeTrainer) // Route to change trainer
            .build();
}



}
