package application.Ports.Drivens.RepositoriesInterfaces;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ScheduleRepositoryInterface {

    Mono<Schedule> createSchedule(Schedule schedule);

    Flux<Schedule> getAllSchedules();

    Flux<Schedule> getScheduleByStartTime(String startTime);

    Flux<Schedule> getScheduleByDay(String day);

    Flux<Schedule> getScheduleByDayGym(String day);

    Mono<Schedule> updateSchedule(Integer id, Schedule updatedSchedule);

    Mono<String> deleteSchedule(Integer id);
}
