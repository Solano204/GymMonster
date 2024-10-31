package infraestrucutre.Adapters.Drivers.Controllers;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.PoolHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
@Configuration
public class RoutersPool {
        
    @Bean
    public RouterFunction<ServerResponse> poolRoutes(PoolHandler poolHandler) {
        return route()
                .POST("/api/pools", poolHandler::createPool)
                .GET("/api/pools", poolHandler::getAllPools)
                .GET("/api/pools/{name}", poolHandler::getPoolById)
                .PUT("/api/pools/{id}", poolHandler::updatePool)
                .DELETE("/api/pools/{name}", poolHandler::deletePool)
                .build();
    }
}

