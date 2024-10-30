package infraestrucutre.Adapters.Drivers.Controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.EquipamentHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class EquipamentRouter {

    @Bean
    public RouterFunction<ServerResponse> equipamentRoutes(EquipamentHandler handler) {
        return RouterFunctions
                .nest(path("/api/admin/equipaments"),
                        route(POST("/create"), handler::createEquipament)
                        .andRoute(GET("/all"), handler::getAllEquipaments)
                        .andRoute(GET("/{name}"), handler::getEquipamentByName)
                        .andRoute(PUT("/update/{id}"), handler::updateEquipament)
                        .andRoute(DELETE("/delete/{name}"), handler::deleteEquipamentByName)
                );
    }
}
