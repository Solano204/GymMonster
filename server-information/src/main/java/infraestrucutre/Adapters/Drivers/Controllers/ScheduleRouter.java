package infraestrucutre.Adapters.Drivers.Controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.ScheduleHandler;
import reactor.core.publisher.Flux;

import static org.springframework.web.reactive.function.server.RouterFunctions.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import org.springframework.web.reactive.function.server.RouterFunction;

@Component
public class ScheduleRouter {

    private final ScheduleHandler scheduleHandler;

    public ScheduleRouter(ScheduleHandler scheduleHandler) {
        this.scheduleHandler = scheduleHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> scheduleRoutes(ScheduleHandler scheduleHandler) {
        return route(POST("/api/schedules"), scheduleHandler::createSchedule) // Create a new schedule
                .andRoute(GET("/api/schedules"), scheduleHandler::getAllSchedules) // Get all schedules
                .andRoute(GET("/api/schedules/start-time/{startTime}"), scheduleHandler::getScheduleByStartTime) // Get schedule by start time
                .andRoute(GET("/api/schedules/day/{day}"), scheduleHandler::getScheduleByDay) // Get schedule by day
                .andRoute(GET("/api/schedules/day/gym/{day}"), scheduleHandler::getScheduleByDayGym) // Get schedule by day
                .andRoute(PUT("/api/schedules/{id}"), scheduleHandler::updateSchedule) // Update schedule by ID
                .andRoute(DELETE("/api/schedules/{id}"), scheduleHandler::deleteSchedule); // Delete schedule by ID
    }
    
}   