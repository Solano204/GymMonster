package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import infraestrucutre.Adapters.Drivens.Entities.DetailUser;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Repositories.ScheduleRepository;
import infraestrucutre.Adapters.Drivens.Repositories.WorkClassRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class WorkClassServiceTest {

    @Mock
    private WorkClassRepository workClassRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    private WorkClassService service;

    @BeforeEach
    void setUp() {
        service = new WorkClassService(workClassRepository, scheduleRepository);
    }

    private WorkClass sample() {
        WorkClass workClass = new WorkClass();
        workClass.setId(1L);
        workClass.setName("Yoga");
        workClass.setDescription("desc");
        workClass.setDuration("60min");
        return workClass;
    }

    @Test
    void createWorkClass_delegatesToSave() {
        WorkClass workClass = sample();
        when(workClassRepository.save(workClass)).thenReturn(Mono.just(workClass));

        StepVerifier.create(service.createWorkClass(workClass)).expectNext(workClass).verifyComplete();
    }

    @Test
    void getAllWorkClasses_delegatesToFindAll() {
        when(workClassRepository.findAll()).thenReturn(Flux.just(sample()));

        StepVerifier.create(service.getAllWorkClasses()).expectNextCount(1).verifyComplete();
    }

    @Test
    void getWorkClassById_delegatesToFindById() {
        WorkClass workClass = sample();
        when(workClassRepository.findById(1L)).thenReturn(Mono.just(workClass));

        StepVerifier.create(service.getWorkClassById(1L)).expectNext(workClass).verifyComplete();
    }

    @Test
    void getWorkClassByName_delegatesToFindByName() {
        WorkClass workClass = sample();
        when(workClassRepository.findByName("Yoga")).thenReturn(Mono.just(workClass));

        StepVerifier.create(service.getWorkClassByName("Yoga")).expectNext(workClass).verifyComplete();
    }

    @Test
    void updateWorkClass_forcesTheIdOntoTheGivenObjectBeforeSaving() {
        WorkClass incoming = new WorkClass();
        incoming.setName("Pilates");
        incoming.setDescription("desc");
        incoming.setDuration("45min");
        when(workClassRepository.save(incoming)).thenReturn(Mono.just(incoming));

        StepVerifier.create(service.updateWorkClass(7L, incoming))
                .assertNext(saved -> assertThat(saved.getId()).isEqualTo(7L))
                .verifyComplete();
        verify(workClassRepository).save(incoming);
    }

    @Test
    void deleteWorkClass_delegatesToDeleteById() {
        when(workClassRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteWorkClass(1L)).verifyComplete();
    }

    @Test
    void getWorkClassSchedules_resolvesNameToIdThenFetchesSchedules() {
        WorkClass workClass = sample();
        when(workClassRepository.findByName("Yoga")).thenReturn(Mono.just(workClass));
        when(scheduleRepository.findSchedulesByWorkClassId(1L)).thenReturn(Flux.just(new Schedule(1, "MONDAY", "08:00", "09:00")));

        StepVerifier.create(service.getWorkClassSchedules("Yoga")).expectNextCount(1).verifyComplete();
    }

    @Test
    void getClientsByWorkClassWithPagination_convertsPageAndSizeToOffset() {
        WorkClass workClass = sample();
        when(workClassRepository.findByName("Yoga")).thenReturn(Mono.just(workClass));
        when(workClassRepository.findAllClientsByWorkClass(1L, 10, 20))
                .thenReturn(Flux.just(new DetailUser()));

        StepVerifier.create(service.getClientsByWorkClassWithPagination("Yoga", 2, 10))
                .expectNextCount(1)
                .verifyComplete();
        verify(workClassRepository).findAllClientsByWorkClass(1L, 10, 20);
    }

    @Test
    void getTrainersByWorkClassWithPagination_convertsPageAndSizeToOffset() {
        WorkClass workClass = sample();
        when(workClassRepository.findByName("Yoga")).thenReturn(Mono.just(workClass));
        when(workClassRepository.findAllTrainersByWorkClass(1L, 10, 0))
                .thenReturn(Flux.just(new DetailUser()));

        StepVerifier.create(service.getTrainersByWorkClassWithPagination("Yoga", 0, 10))
                .expectNextCount(1)
                .verifyComplete();
    }
}
