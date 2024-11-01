package infraestrucutre.Adapters.Drivers.Controllers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.WorkClassHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class WorkClassEndpoints {

    private final WorkClassHandler workClassHandler;

    public WorkClassEndpoints(WorkClassHandler workClassHandler) {
        this.workClassHandler = workClassHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> workClassRoutes() {
        return route()
                .POST("/api/workclasses", workClassHandler::createWorkClass) // Create a new WorkClass
                .GET("/api/workclasses", workClassHandler::getAllWorkClasses) // Get all WorkClasses
                .GET("/api/workclasses/{name}", workClassHandler::getWorkClassByName) // Get WorkClass by ID
                .PUT("/api/workclasses/{name}", workClassHandler::updateWorkClass) // Update a WorkClass
                .DELETE("/api/workclasses/{name}", workClassHandler::deleteWorkClass) // Delete a WorkClass
                .GET("/api/workclasses/{name}/schedules", workClassHandler::getWorkClassSchedules) // Get schedules for WorkClass
                .GET("/api/workclasses/{name}/clients",                                                                                 workClassHandler::getClientsByWorkClassWithPagination) // Get Clients with pagination
                .GET("/api/workclasses/{name}/trainers", workClassHandler::getTrainersByWorkClassWithPagination) // Get Trainers with pagination
                .build();
    }
}
