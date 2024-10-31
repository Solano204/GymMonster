package application.Ports.Drivers.IServices;

import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ScheduleInterface {
    
    // Create a new schedule
    Mono<Schedule> createSchedule(Schedule schedule);

    // Get all schedules
    Flux<Schedule> getAllSchedules();

    // Get schedule by start time
    Flux<Schedule> getScheduleByStartTime(String startTime);

    // Get schedule by day
    Flux<Schedule> getScheduleByDay(String day);

    // Get schedule by day for gym
    Flux<Schedule> getScheduleByDayGym(String day);

    // Update schedule by ID
    Mono<Schedule> updateSchedule(Integer id, Schedule updatedSchedule);

    // Delete schedule by ID
    Mono<String> deleteSchedule(Integer id);
}
