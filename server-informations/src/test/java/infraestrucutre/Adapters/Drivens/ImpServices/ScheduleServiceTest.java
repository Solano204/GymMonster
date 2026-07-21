package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Repositories.ScheduleRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    private ScheduleService service;

    @BeforeEach
    void setUp() {
        service = new ScheduleService(scheduleRepository);
    }

    private Schedule sample() {
        return new Schedule(1, "MONDAY", "08:00", "09:00");
    }

    @Test
    void createSchedule_delegatesToSave() {
        Schedule schedule = sample();
        when(scheduleRepository.save(schedule)).thenReturn(Mono.just(schedule));

        StepVerifier.create(service.createSchedule(schedule)).expectNext(schedule).verifyComplete();
    }

    @Test
    void getAllSchedules_delegatesToFindAll() {
        when(scheduleRepository.findAll()).thenReturn(Flux.just(sample()));

        StepVerifier.create(service.getAllSchedules()).expectNextCount(1).verifyComplete();
    }

    @Test
    void getScheduleBy_delegatesToFindById() {
        Schedule schedule = sample();
        when(scheduleRepository.findById(1)).thenReturn(Mono.just(schedule));

        StepVerifier.create(service.getScheduleBy(1)).expectNext(schedule).verifyComplete();
    }

    @Test
    void getScheduleByStartTime_delegatesToFindByStartTime() {
        when(scheduleRepository.findByStartTime("08:00")).thenReturn(Flux.just(sample()));

        StepVerifier.create(service.getScheduleByStartTime("08:00")).expectNextCount(1).verifyComplete();
    }

    @Test
    void getScheduleByDay_delegatesToFindByDay() {
        when(scheduleRepository.findByDay("MONDAY")).thenReturn(Flux.just(sample()));

        StepVerifier.create(service.getScheduleByDay("MONDAY")).expectNextCount(1).verifyComplete();
    }

    @Test
    void updateSchedule_forcesTheIdOntoTheGivenObjectBeforeSaving() {
        Schedule incoming = new Schedule(null, "TUESDAY", "10:00", "11:00");
        when(scheduleRepository.save(incoming)).thenReturn(Mono.just(incoming));

        StepVerifier.create(service.updateSchedule(5, incoming))
                .assertNext(saved -> assertThat(saved.getId()).isEqualTo(5))
                .verifyComplete();
        verify(scheduleRepository).save(incoming);
    }

    @Test
    void deleteSchedule_delegatesToDeleteById() {
        when(scheduleRepository.deleteById(1)).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteSchedule(1)).verifyComplete();
    }

    @Test
    void getSchedulesByDayGym_delegatesToFindSchedulesGym() {
        when(scheduleRepository.findSchedulesGym("MONDAY")).thenReturn(Flux.just(sample()));

        StepVerifier.create(service.getSchedulesByDayGym("MONDAY")).expectNextCount(1).verifyComplete();
    }
}
