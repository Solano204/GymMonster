package infraestrucutre.Adapters.Drivers.Controllers;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.SpecialtyHandler;
import lombok.Data;
import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Data
public class RouterSpecialty {

   private final SpecialtyHandler specialtyHandler;


   @Bean
public RouterFunction<ServerResponse> routesSpecialty() {
    return RouterFunctions
        .route()
        .GET("/api/admin/specialties", specialtyHandler::getAllSpecialties) // Route to get all specialties
        .GET("/api/admin/trainers", specialtyHandler::getAllTrainers) // Route to get all trainers
        .POST("/api/admin/specialties/create", specialtyHandler::createSpecialty) // Route to create a specialty
        .GET("/api/admin/specialties/{name}", specialtyHandler::getSpecialtyByName) // Route to get specialty by name
        .PUT("/api/admin/specialties/{name}/update", specialtyHandler::updateSpecialty) // Route to update specialty
        .DELETE("/api/admin/specialties/{name}/delete", specialtyHandler::deleteSpecialty) // Route to delete specialty
        .build();
}

}