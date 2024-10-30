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
import infraestrucutre.Adapters.Drivens.Handlers.PoolHandler;
import infraestrucutre.Adapters.Drivens.Handlers.RegisterClientHandler;
import reactor.core.publisher.Mono;
// Define the routes

@Configuration
public class RouterPool {
    @Bean
    public RouterFunction<ServerResponse> poolRoutes(PoolHandler poolHandler) {
        return route(GET("/api/page/allPools"), poolHandler::getAllPools); // Get all pools
    }
}
