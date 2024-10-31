package infraestrucutre.Adapters.Drivers.Controllers;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.MembershipHandler;
import infraestrucutre.Adapters.Drivens.Handlers.RegisterClientHandler;
import infraestrucutre.Adapters.Drivens.Handlers.TrainerHandler;
import reactor.core.publisher.Mono;

// Define the routes

@Configuration
public class preTrainers {

    @Bean
    public RouterFunction<ServerResponse> trainerRoutes(TrainerHandler trainerHandler) {
        return route(GET("/api/page/allTrainers"), trainerHandler::getAllTrainers)
                .andRoute(GET("/api/page/trainers/{username}/specialties"), trainerHandler::getAllSpecialtiesFromTrainer);
    }

}