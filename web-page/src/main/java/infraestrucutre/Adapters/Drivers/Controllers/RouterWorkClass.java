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
import infraestrucutre.Adapters.Drivens.Handlers.WorkClassHandler;
import reactor.core.publisher.Mono;


@Configuration
public class RouterWorkClass {
    
    // Define the routes
    @Bean
    public RouterFunction<ServerResponse> workClassRoutes(WorkClassHandler workClassHandler) {
        return route(GET("/api/page/workclasses"), workClassHandler::getAllWorkClasses)
        .andRoute(GET("/api/page/workclasses/{name}/schedules"), workClassHandler::getWorkClassSchedules)
        .andRoute(GET("/api/page/workclasses/{name}/clients"), workClassHandler::getClientsByWorkClass) //
        .andRoute(GET("/api/page/workclasses/{name}/trainers"), workClassHandler::getTrainersByWorkClass);
        
    }
    
}