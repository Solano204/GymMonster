package infraestrucutre.Adapters.Drivens.ImpServices;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import application.Ports.Drivens.RepositoriesInterfaces.ScheduleRepositoryInterface;
import application.Ports.Drivers.IServices.ScheduleInterface;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Repositories.ScheduleRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Data
public class ScheduleService implements ScheduleInterface {

    private final ScheduleRepositoryInterface scheduleRepository;

    // In-memory cache for schedules (if needed)
    // Create a new schedule
    @Override
    public Mono<Schedule> createSchedule(Schedule schedule) {
        return scheduleRepository.createSchedule(schedule);
    }

    // Get all schedules
    @Override
    public Flux<Schedule> getAllSchedules() {
        return scheduleRepository.getAllSchedules();
    }

    // Get schedule by start time
    @Override
    public Flux<Schedule> getScheduleByStartTime(String startTime) {
        return scheduleRepository.getScheduleByStartTime(startTime);
    }

    // Get schedule by day
    @Override
    public Flux<Schedule> getScheduleByDay(String day) {
        return scheduleRepository.getScheduleByDay(day);
    }

    // Get schedule by day for gym
    @Override
    public Flux<Schedule> getScheduleByDayGym(String day) {
        return scheduleRepository.getScheduleByDayGym(day);
    }

    // Update schedule by ID
    @Override
    public Mono<Schedule> updateSchedule(Integer id, Schedule updatedSchedule) {
        return scheduleRepository.updateSchedule(id, updatedSchedule);
    }

    // Delete schedule by ID
    @Override
    public Mono<String> deleteSchedule(Integer id) {
        return scheduleRepository.deleteSchedule(id);
    }
}
