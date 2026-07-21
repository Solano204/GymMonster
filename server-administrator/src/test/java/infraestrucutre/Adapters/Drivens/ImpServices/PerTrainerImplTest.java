package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import application.Ports.Drivens.RepositoriesInterfaces.PerTrainerInformationClientInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PerTrainerImplTest {

    @Mock
    private PerTrainerInformationClientInterface clientRepository;

    private PerTrainerImpl service;

    @BeforeEach
    void setUp() {
        service = new PerTrainerImpl(clientRepository);
    }

    private AllTrainer sampleTrainer() {
        return new AllTrainer(1L, "coach99", "pw", "coach@test.com", "Ana", "M", "Lopez", "Diaz",
                "35", "170", "65", LocalDate.now());
    }

    @Test
    void createPerTrainer_delegatesToRepository() {
        AllTrainer trainer = sampleTrainer();
        when(clientRepository.createPerTrainer(trainer)).thenReturn(Mono.just("Created"));

        StepVerifier.create(service.createPerTrainer(trainer)).expectNext("Created").verifyComplete();
    }

    @Test
    void getAllTrainers_delegatesToRepositoryWithPageAndSize() {
        DtoTrainerData trainerData = new DtoTrainerData(1L, "coach99", "coach@test.com", "Ana", "M", "Lopez",
                "Diaz", "35", "170", "65", LocalDate.now(), 5);
        when(clientRepository.getAllTrainers(0, 10)).thenReturn(Flux.just(trainerData));

        StepVerifier.create(service.getAllTrainers(0, 10)).expectNext(trainerData).verifyComplete();
    }

    @Test
    void deletePerTrainer_delegatesToRepository() {
        when(clientRepository.deletePerTrainer("coach99", "pw")).thenReturn(Mono.just("Deleted"));

        StepVerifier.create(service.deletePerTrainer("coach99", "pw")).expectNext("Deleted").verifyComplete();
    }

    @Test
    void getAllClientsFromTrainer_delegatesToRepositoryWithUsernamePageAndSize() {
        DtoDetailUserReciving client = new DtoDetailUserReciving("John", "Q", "Doe", "Public", "30", "80", "180");
        when(clientRepository.getAllClientsFromTrainer("coach99", 0, 10)).thenReturn(Flux.just(client));

        StepVerifier.create(service.getAllClientsFromTrainer("coach99", 0, 10))
                .expectNext(client)
                .verifyComplete();
    }

    @Test
    void getAllSpecialtiesFromTrainer_delegatesToRepository() {
        List<DtoSpecialtyRecived> specialties = List.of(new DtoSpecialtyRecived("Yoga", "desc"));
        when(clientRepository.getAllSpecialtiesFromTrainer("coach99")).thenReturn(Mono.just(specialties));

        StepVerifier.create(service.getAllSpecialtiesFromTrainer("coach99")).expectNext(specialties).verifyComplete();
    }

    @Test
    void updateTrainerPassword_delegatesToRepositoryPreservingArgumentOrder() {
        when(clientRepository.updateTrainerPassword("coach99", "new-pw", "old-pw")).thenReturn(Mono.just("Password updated"));

        StepVerifier.create(service.updateTrainerPassword("coach99", "new-pw", "old-pw"))
                .expectNext("Password updated")
                .verifyComplete();
        verify(clientRepository).updateTrainerPassword("coach99", "new-pw", "old-pw");
    }

    @Test
    void updateEmail_delegatesToRepository() {
        when(clientRepository.updateEmail("coach99", "new@test.com")).thenReturn(Mono.just("Email updated"));

        StepVerifier.create(service.updateEmail("coach99", "new@test.com"))
                .expectNext("Email updated")
                .verifyComplete();
    }

    @Test
    void updateTrainerUsername_delegatesToRepository() {
        when(clientRepository.updateTrainerUsername("coach99", "coach100")).thenReturn(Mono.just("Username updated"));

        StepVerifier.create(service.updateTrainerUsername("coach99", "coach100"))
                .expectNext("Username updated")
                .verifyComplete();
    }

    @Test
    void updateTrainerDetails_delegatesToRepository() {
        DtoDetailUserReciving details = new DtoDetailUserReciving("Ana", "M", "Lopez", "Diaz", "35", "65", "170");
        when(clientRepository.updateTrainerDetails("coach99", details)).thenReturn(Mono.just("Updated"));

        StepVerifier.create(service.updateTrainerDetails("coach99", details))
                .expectNext("Updated")
                .verifyComplete();
    }

    @Test
    void addSpecialtyToTrainer_delegatesToRepository() {
        when(clientRepository.addSpecialtyToTrainer("coach99", "Yoga")).thenReturn(Mono.just("Specialty added"));

        StepVerifier.create(service.addSpecialtyToTrainer("coach99", "Yoga"))
                .expectNext("Specialty added")
                .verifyComplete();
    }

    @Test
    void removeSpecialtyFromTrainer_delegatesToRepository() {
        when(clientRepository.removeSpecialtyFromTrainer("coach99", "Yoga")).thenReturn(Mono.just("Specialty removed"));

        StepVerifier.create(service.removeSpecialtyFromTrainer("coach99", "Yoga"))
                .expectNext("Specialty removed")
                .verifyComplete();
    }
}
