package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import application.Ports.Drivens.RepositoriesInterfaces.ScheduleInformationClientInterface;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleInformationClientInterface scheduleRepository;

    private ScheduleService service;

    @BeforeEach
    void setUp() {
        service = new ScheduleService(scheduleRepository);
    }

    private Schedule sample() {
        return new Schedule(1, "MONDAY", "08:00", "09:00");
    }

    @Test
    void createSchedule_delegatesToRepository() {
        Schedule schedule = sample();
        when(scheduleRepository.createSchedule(schedule)).thenReturn(Mono.just(schedule));

        StepVerifier.create(service.createSchedule(schedule)).expectNext(schedule).verifyComplete();
    }

    @Test
    void getAllSchedules_delegatesToRepository() {
        Schedule schedule = sample();
        when(scheduleRepository.getAllSchedules()).thenReturn(Flux.just(schedule));

        StepVerifier.create(service.getAllSchedules()).expectNext(schedule).verifyComplete();
    }

    @Test
    void getScheduleByStartTime_delegatesToRepository() {
        Schedule schedule = sample();
        when(scheduleRepository.getScheduleByStartTime("08:00")).thenReturn(Flux.just(schedule));

        StepVerifier.create(service.getScheduleByStartTime("08:00")).expectNext(schedule).verifyComplete();
    }

    @Test
    void getScheduleByDay_delegatesToRepository() {
        Schedule schedule = sample();
        when(scheduleRepository.getScheduleByDay("MONDAY")).thenReturn(Flux.just(schedule));

        StepVerifier.create(service.getScheduleByDay("MONDAY")).expectNext(schedule).verifyComplete();
    }

    @Test
    void getScheduleByDayGym_delegatesToRepository() {
        Schedule schedule = sample();
        when(scheduleRepository.getScheduleByDayGym("MONDAY")).thenReturn(Flux.just(schedule));

        StepVerifier.create(service.getScheduleByDayGym("MONDAY")).expectNext(schedule).verifyComplete();
    }

    @Test
    void updateSchedule_delegatesToRepositoryWithIdAndPayload() {
        Schedule schedule = sample();
        when(scheduleRepository.updateSchedule(1, schedule)).thenReturn(Mono.just(schedule));

        StepVerifier.create(service.updateSchedule(1, schedule)).expectNext(schedule).verifyComplete();
        verify(scheduleRepository).updateSchedule(1, schedule);
    }

    @Test
    void deleteSchedule_delegatesToRepository() {
        when(scheduleRepository.deleteSchedule(1)).thenReturn(Mono.just("Deleted"));

        StepVerifier.create(service.deleteSchedule(1)).expectNext("Deleted").verifyComplete();
    }
}
