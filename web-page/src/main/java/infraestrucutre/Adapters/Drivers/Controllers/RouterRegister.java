package infraestrucutre.Adapters.Drivers.Controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.RegisterClientHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRegister {
    @Bean
    public RouterFunction<ServerResponse> clientRoutes(RegisterClientHandler clientHandler) {
        return route(POST("/api/page/registerClient"), clientHandler::createClient)
                .andRoute(PUT("/api/page/clients/{username}/{oldPassword}/{newPassword}/changePassword"),
                        clientHandler::updateClientPassword)    
                .andRoute(PUT("/api/page/clients/{username}/{email}/changeEmail"), clientHandler::updateClientEmail)
                .andRoute(PUT("/api/page/clients/{username}/{membershipType}/changeMembership"),
                        clientHandler::updateClientMembership)
                .andRoute(PUT("/api/page/clients/{username}/{newUsernameTrainer}/changeTrainer"),
                        clientHandler::updateClientTrainer)
                .andRoute(PUT("/api/page/clients/{username}/{newUsername}/changeUsername"),
                        clientHandler::updateClientUsername)
                .andRoute(PUT("/api/page/clients/{username}/changeInformation"), clientHandler::updateClientAllInformation)
                .andRoute(DELETE("/api/page/clients/{username}/{password}/deleteAccount"), clientHandler::deleteClient)
                .andRoute(DELETE("/api/page/clients/{username}/{membershipType}/delete-membership"), clientHandler::removeMembership)
                .andRoute(GET("/api/page/clients/{username}/allClass"), clientHandler::getAllClass)
                .andRoute(GET("/api/page/clients/{username}/allInformation"), clientHandler::getClient)
                        .andRoute(DELETE("/api/page/clients/{username}/{trainerUsername}/remove-trainer"),
                                        clientHandler::removeTrainer)
                                        ;

    }
}
