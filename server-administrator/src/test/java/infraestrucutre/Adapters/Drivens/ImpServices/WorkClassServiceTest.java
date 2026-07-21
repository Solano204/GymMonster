package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import application.Ports.Drivens.RepositoriesInterfaces.WorkClassInformationClientInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class WorkClassServiceTest {

    @Mock
    private WorkClassInformationClientInterface workClassRepository;

    private WorkClassService service;

    @BeforeEach
    void setUp() {
        service = new WorkClassService(workClassRepository);
    }

    private WorkClass sampleWorkClass() {
        return new WorkClass(1L, "Yoga", "Relaxing class", "60min");
    }

    @Test
    void getAllWorkClasses_delegatesToRepository() {
        WorkClass workClass = sampleWorkClass();
        when(workClassRepository.getAllWorkClasses()).thenReturn(Flux.just(workClass));

        StepVerifier.create(service.getAllWorkClasses()).expectNext(workClass).verifyComplete();
    }

    @Test
    void updateWorkClass_delegatesToRepositoryWithSameArgumentOrder() {
        WorkClass workClass = sampleWorkClass();
        when(workClassRepository.updateWorkClass(workClass, "Yoga")).thenReturn(Mono.just(workClass));

        StepVerifier.create(service.updateWorkClass(workClass, "Yoga")).expectNext(workClass).verifyComplete();
        verify(workClassRepository).updateWorkClass(workClass, "Yoga");
    }

    @Test
    void deleteWorkClass_delegatesToRepository() {
        when(workClassRepository.deleteWorkClass("Yoga")).thenReturn(Mono.just("Deleted"));

        StepVerifier.create(service.deleteWorkClass("Yoga")).expectNext("Deleted").verifyComplete();
    }

    @Test
    void getClientsByWorkClassWithPagination_delegatesToRepositoryWithPageAndSize() {
        DtoDetailUserReciving client = new DtoDetailUserReciving("John", "Q", "Doe", "Public", "30", "80", "180");
        when(workClassRepository.getClientsByWorkClassWithPagination("Yoga", 0, 10)).thenReturn(Flux.just(client));

        StepVerifier.create(service.getClientsByWorkClassWithPagination("Yoga", 0, 10))
                .expectNext(client)
                .verifyComplete();
    }

    @Test
    void getTrainersByWorkClassWithPagination_delegatesToRepositoryWithPageAndSize() {
        DtoDetailUserReciving trainer = new DtoDetailUserReciving("Coach", "Q", "Doe", "Public", "30", "80", "180");
        when(workClassRepository.getTrainersByWorkClassWithPagination("Yoga", 0, 10)).thenReturn(Flux.just(trainer));

        StepVerifier.create(service.getTrainersByWorkClassWithPagination("Yoga", 0, 10))
                .expectNext(trainer)
                .verifyComplete();
    }

    @Test
    void createWorkclass_delegatesToRepository() {
        WorkClass workClass = sampleWorkClass();
        when(workClassRepository.createWorkclass(workClass)).thenReturn(Mono.just(workClass));

        StepVerifier.create(service.createWorkclass(workClass)).expectNext(workClass).verifyComplete();
    }

    @Test
    void getCSchedulesByWorkClassWithPagination_delegatesToRepository() {
        Schedule schedule = new Schedule(1, "MONDAY", "08:00", "09:00");
        when(workClassRepository.getCSchedulesByWorkClassWithPagination("Yoga")).thenReturn(Flux.just(schedule));

        StepVerifier.create(service.getCSchedulesByWorkClassWithPagination("Yoga"))
                .expectNext(schedule)
                .verifyComplete();
    }
}
