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
import reactor.core.publisher.Mono;
// Define the routes

@Configuration
public class RouterPool {
    @Bean
public RouterFunction<ServerResponse> poolRoutes(PoolHandler poolHandler) {
    return RouterFunctions.route()
            .GET("/api/admin/allPools", poolHandler::getAllPools) // Get all pools
            .POST("/api/admin/pools/create", poolHandler::createPool) // Create a new pool
            .PUT("/api/admin/pools/{name}/update", poolHandler::updatePool) // Update a pool by ID
            .DELETE("/api/admin/pools/{name}/delete", poolHandler::deletePoolByName) // Delete a pool by ID
            .build();
}

}
