package infraestrucutre.Adapters.Drivers.Controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.SpecialtyHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class SpecialtyRouter {

    @Bean
    public RouterFunction<ServerResponse> specialtyRoutes(SpecialtyHandler specialtyHandler) {
        return RouterFunctions
            .nest(path("/api/specialties"), route()
                // Create a new Specialty
                .POST("", specialtyHandler::createSpecialty)
                
                // Get all Specialties
                .GET("", specialtyHandler::getAllSpecialties)
                
                // Get a Specialty by ID
                .GET("/{name}", specialtyHandler::getSpecialtyByName)
                
                // Update a Specialty by ID
                .PUT("/{name}", specialtyHandler::updateSpecialty)
                
                // Delete a Specialty by ID
                .DELETE("/{name}", specialtyHandler::deleteSpecialtyName)
                .build()
            );
    }
}
