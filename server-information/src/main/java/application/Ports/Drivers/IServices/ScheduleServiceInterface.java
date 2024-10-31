package application.Ports.Drivers.IServices;

import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ScheduleServiceInterface {

    Mono<Schedule> createSchedule(Schedule schedule);

    Flux<Schedule> getAllSchedules();

    Mono<Schedule> getScheduleBy(Integer id);

    Flux<Schedule> getScheduleByStartTime(String startTime);

    Flux<Schedule> getScheduleByDay(String day);
    Flux<Schedule> getSchedulesByDayGym(String day);

    Mono<Schedule> updateSchedule(Integer id, Schedule schedule);

    Mono<Void> deleteSchedule(Integer id);
}
