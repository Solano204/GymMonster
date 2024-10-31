package infraestrucutre.Adapters.Drivers.Controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Handlers.ScheduleHandler;
import lombok.Data;
import reactor.core.publisher.Flux;

import static org.springframework.web.reactive.function.server.RouterFunctions.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Data
public class ScheduleRouter {

    private final ScheduleHandler scheduleHandler;
    @Bean
public RouterFunction<ServerResponse> routesSchedule() {
    return RouterFunctions
            .route()
            .POST("/api/admin/schedules", scheduleHandler::createSchedule) // Create a new schedule
            .GET("/api/admin/schedules", scheduleHandler::getAllSchedules) // Get all schedules
            .GET("/api/admin/schedules/start/{startTime}", scheduleHandler::getScheduleByStartTime) // Get schedule by start time
            .GET("/api/admin/schedules/day/{day}", scheduleHandler::getScheduleByDay) // Get schedule by day
            .GET("/api/admin/schedules/gym/day/{day}", scheduleHandler::getScheduleByDayGym) // Get gym schedule by day
            .PUT("/api/admin/schedules/{id}", scheduleHandler::updateSchedule) // Update a schedule
            .DELETE("/api/admin/schedules/{id}", scheduleHandler::deleteSchedule) // Delete a schedule
            .build();
}

}
