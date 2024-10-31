package infraestrucutre.Adapters.Drivers.Controllers;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.WorkClassHandler;
import reactor.core.publisher.Mono;


@Configuration
public class RouterWorkClass {
    
    
    @Bean
    public RouterFunction<ServerResponse> routeWorkClass(WorkClassHandler handler) {
        return RouterFunctions
                .route(GET("/api/admin/work-classes/all"), handler::getAllWorkClasses) // Get all work classes
                .andRoute(GET("/api/admin/work-class/{name}/schedules"), handler::getWorkClassSchedules) // Get schedules for a specific work class
                .andRoute(GET("/api/admin/work-class/{name}/clients"), handler::getClientsByWorkClass) // Get clients by work class
                .andRoute(GET("/api/admin/work-class/{name}/trainers"), handler::getTrainersByWorkClass) // Get trainers by work class
                .andRoute(POST("/api/admin/work-class/create"), handler::createWorkClass) // Create a new work class
                .andRoute(PUT("/api/admin/work-class/{name}/update"), handler::updateWorkClass) // Update a work class
                .andRoute(DELETE("/api/admin/work-class/{name}/delete"), handler::deleteWorkClass);
    }
    
    
    
}