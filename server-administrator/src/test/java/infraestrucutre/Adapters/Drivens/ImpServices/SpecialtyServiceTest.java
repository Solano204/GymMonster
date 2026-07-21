package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import application.Ports.Drivens.RepositoriesInterfaces.SpecialtyInformationClientInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceTest {

    @Mock
    private SpecialtyInformationClientInterface specialtyRepository;

    private SpecialtyService service;

    @BeforeEach
    void setUp() {
        service = new SpecialtyService(specialtyRepository);
    }

    private DtoSpecialtyRecived sample() {
        return new DtoSpecialtyRecived("Yoga", "Relaxing class");
    }

    @Test
    void getAllSpecialties_delegatesToRepository() {
        DtoSpecialtyRecived specialty = sample();
        when(specialtyRepository.getAllSpecialties()).thenReturn(Flux.just(specialty));

        StepVerifier.create(service.getAllSpecialties()).expectNext(specialty).verifyComplete();
    }

    @Test
    void getAllTrainers_delegatesToRepository() {
        DtoSpecialtyRecived specialty = sample();
        when(specialtyRepository.getAllTrainers()).thenReturn(Flux.just(specialty));

        StepVerifier.create(service.getAllTrainers()).expectNext(specialty).verifyComplete();
    }

    @Test
    void createSpecialty_delegatesToRepository() {
        DtoSpecialtyRecived specialty = sample();
        when(specialtyRepository.createSpecialty(specialty)).thenReturn(Mono.just(specialty));

        StepVerifier.create(service.createSpecialty(specialty)).expectNext(specialty).verifyComplete();
    }

    @Test
    void getSpecialtyByName_delegatesToRepository() {
        DtoSpecialtyRecived specialty = sample();
        when(specialtyRepository.getSpecialtyByName("Yoga")).thenReturn(Mono.just(specialty));

        StepVerifier.create(service.getSpecialtyByName("Yoga")).expectNext(specialty).verifyComplete();
    }

    @Test
    void updateSpecialty_delegatesToRepositoryWithNameAndPayload() {
        DtoSpecialtyRecived specialty = sample();
        when(specialtyRepository.updateSpecialty("Yoga", specialty)).thenReturn(Mono.just("Updated"));

        StepVerifier.create(service.updateSpecialty("Yoga", specialty)).expectNext("Updated").verifyComplete();
        verify(specialtyRepository).updateSpecialty("Yoga", specialty);
    }

    @Test
    void deleteSpecialty_delegatesToRepository() {
        when(specialtyRepository.deleteSpecialty("Yoga")).thenReturn(Mono.just("Deleted"));

        StepVerifier.create(service.deleteSpecialty("Yoga")).expectNext("Deleted").verifyComplete();
    }
}
