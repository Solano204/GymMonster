package infraestrucutre.Adapters.Drivens.ImpServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import application.Ports.Drivers.IServices.ScheduleServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.Pool;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Repositories.PoolRepository;
import infraestrucutre.Adapters.Drivens.Repositories.ScheduleRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class ScheduleService implements ScheduleServiceInterface {

    private final ScheduleRepository scheduleRepository;

    @Override
    public Mono<Schedule> createSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Override
    public Flux<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    @Override
    public Mono<Schedule> getScheduleBy(Integer id) {
        return scheduleRepository.findById(id);
    }

    @Override
    public Flux<Schedule> getScheduleByStartTime(String startTime) {
        return scheduleRepository.findByStartTime(startTime);
    }

    @Override
    public Flux<Schedule> getScheduleByDay(String day) {
        return scheduleRepository.findByDay(day);
    }

    @Override
    public Mono<Schedule> updateSchedule(Integer id, Schedule schedule) {
        schedule.setId(id);
        return scheduleRepository.save(schedule);
    }

    @Override
    public Mono<Void> deleteSchedule(Integer id) {
        return scheduleRepository.deleteById(id);
    }

    @Override
    public Flux<Schedule> getSchedulesByDayGym(String day) {
        return scheduleRepository.findSchedulesGym(day);
    }
}
