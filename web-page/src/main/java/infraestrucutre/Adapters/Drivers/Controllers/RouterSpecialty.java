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
import infraestrucutre.Adapters.Drivens.Handlers.SpecialtyHandler;
import reactor.core.publisher.Mono;

@Configuration
public class RouterSpecialty {

    @Bean
    public RouterFunction<ServerResponse> specialtyRoutes(SpecialtyHandler specialtyHandler) {
        return route(GET("/api/page/allSpecialties"), specialtyHandler::getAllSpecialties); // Get all specialties by
        // username
    }
    
}