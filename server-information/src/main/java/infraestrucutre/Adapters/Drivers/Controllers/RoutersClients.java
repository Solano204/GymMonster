package infraestrucutre.Adapters.Drivers.Controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.ClientHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RoutersClients {

        @Bean
        public RouterFunction<ServerResponse> clientRoutes(ClientHandler clientHandler) {
            return route(POST("/api/clients/AD/create"), clientHandler::createClient) // Create a new client
                    .andRoute(GET("/api/clients/AD/allClients"), clientHandler::getAllClientsAD) // Get all clients // Get client by username
                    .andRoute(GET("/api/clients/{username}/allInformation"), clientHandler:: getClientDetailMembershipByClientId) // Get all clients // Get client by username
                    .andRoute(PUT("/api/clients/{username}/updateBasicInformation"), clientHandler::updateClient) // Update client by username
                    .andRoute(PUT("/api/clients/{username}/{oldPassword}/{newPassword}/changePassword"), clientHandler::updateClientPassword) // Update client password
                    .andRoute(PUT("/api/clients/{username}/{email}/changeEmail"), clientHandler::updateClientEmail) // Update client email
                    .andRoute(PUT("/api/clients/{username}/{newUsername}/{password}/changeUsername"), clientHandler::updateClientUsername) // Update client password
                // Update client email
                    .andRoute(PUT("/api/clients/{username}/{membershipType}/assignMembership"), clientHandler::updateClientMembership) // Update client membership
                    .andRoute(PUT("/api/clients/{username}/{usernameTrainer}/assignTrainer"), clientHandler::updateTrainer) // Update trainer
                    .andRoute(DELETE("/api/clients/{username}/{usernameTrainer}/dessignTrainer"), clientHandler::dessignTrainer) // Assign a trainer
                    .andRoute(DELETE("/api/clients/{username}/{membershipType}/dessignMembership"), clientHandler::dessignMembership) // Assign membership
                    .andRoute(DELETE("/api/clients/{username}/{password}/deleteAccount"), clientHandler::deleteClient) // Delete client by username and password
                    .andRoute(GET("/api/clients/{username}/workClasses"), clientHandler::getWorkClassesByClientId) // Get work classes by client ID
                    .andRoute(GET("/api/clients/{username}/usernameExist"), clientHandler::existsByUsernameClient) // Check if username exists
                    .andRoute(GET("/api/clients/{email}/emailExist"), clientHandler::existsByEmailClient); // Check if email exists
        }
}