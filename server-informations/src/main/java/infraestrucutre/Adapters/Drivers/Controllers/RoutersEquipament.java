package infraestrucutre.Adapters.Drivers.Controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.ClientHandler;
import infraestrucutre.Adapters.Drivens.Handlers.EquipamentHandler;



@Configuration
public class RoutersEquipament {
            @Bean
    public RouterFunction<ServerResponse> equiRoutes( EquipamentHandler  equipamentHandler) {
        return route(POST("/api/equipaments"), equipamentHandler::createEquipament) // Create a new equipament
        .andRoute(GET("/api/equipaments"), equipamentHandler::getAllEquipaments) // Get all equipaments
        .andRoute(GET("/api/equipaments/{name}"), equipamentHandler::getEquipamentByName) // Get equipament by name
        .andRoute(PUT("/api/equipaments/{id}"), equipamentHandler::updateEquipament) // Update equipament by ID
        .andRoute(DELETE("/api/equipaments/{name}"), equipamentHandler::deleteEquipamentByName); // Delete equipament by name

    }
}
