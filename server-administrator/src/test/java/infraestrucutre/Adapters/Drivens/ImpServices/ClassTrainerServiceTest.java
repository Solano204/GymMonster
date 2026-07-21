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

import application.Ports.Drivens.RepositoriesInterfaces.WorkTrainerInformationClientInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ClassTrainerServiceTest {

    @Mock
    private WorkTrainerInformationClientInterface classTrainerRepository;

    private ClassTrainerService service;

    @BeforeEach
    void setUp() {
        service = new ClassTrainerService(classTrainerRepository);
    }

    private AllTrainer sampleTrainer() {
        return new AllTrainer(1L, "coach99", "pw", "coach@test.com", "Ana", "M", "Lopez", "Diaz",
                "35", "170", "65", LocalDate.now());
    }

    @Test
    void createClassTrainer_delegatesToRepository() {
        AllTrainer trainer = sampleTrainer();
        when(classTrainerRepository.createClassTrainer(trainer)).thenReturn(Mono.just("Created"));

        StepVerifier.create(service.createClassTrainer(trainer)).expectNext("Created").verifyComplete();
    }

    @Test
    void getAllTrainers_delegatesToRepositoryWithPageAndSize() {
        DtoTrainerData trainerData = new DtoTrainerData(1L, "coach99", "coach@test.com", "Ana", "M", "Lopez",
                "Diaz", "35", "170", "65", LocalDate.now(), 5);
        when(classTrainerRepository.getAllTrainers(0, 10)).thenReturn(Flux.just(trainerData));

        StepVerifier.create(service.getAllTrainers(0, 10)).expectNext(trainerData).verifyComplete();
    }

    @Test
    void getAllSpecialtiesFromTrainer_delegatesToRepository() {
        List<DtoSpecialtyRecived> specialties = List.of(new DtoSpecialtyRecived("Yoga", "desc"));
        when(classTrainerRepository.getAllSpecialtiesFromTrainer("coach99")).thenReturn(Mono.just(specialties));

        StepVerifier.create(service.getAllSpecialtiesFromTrainer("coach99")).expectNext(specialties).verifyComplete();
    }

    @Test
    void updateTrainerAllDetailInformation_delegatesToRepository() {
        DtoDetailUserReciving details = new DtoDetailUserReciving("Ana", "M", "Lopez", "Diaz", "35", "65", "170");
        when(classTrainerRepository.updateTrainerAllDetailInformation("coach99", details)).thenReturn(Mono.just("Updated"));

        StepVerifier.create(service.updateTrainerAllDetailInformation("coach99", details))
                .expectNext("Updated")
                .verifyComplete();
        verify(classTrainerRepository).updateTrainerAllDetailInformation("coach99", details);
    }

    @Test
    void getWorkClassesByTrainer_delegatesToRepository() {
        List<WorkClass> workClasses = List.of(new WorkClass(1L, "Yoga", "desc", "60min"));
        when(classTrainerRepository.getWorkClassesByTrainer("coach99")).thenReturn(Mono.just(workClasses));

        StepVerifier.create(service.getWorkClassesByTrainer("coach99")).expectNext(workClasses).verifyComplete();
    }

    @Test
    void updateTrainerPassword_delegatesToRepositoryWithAllThreeArguments() {
        when(classTrainerRepository.updateTrainerPassword("coach99", "old", "new")).thenReturn(Mono.just("Password updated"));

        StepVerifier.create(service.updateTrainerPassword("coach99", "old", "new"))
                .expectNext("Password updated")
                .verifyComplete();
        verify(classTrainerRepository).updateTrainerPassword("coach99", "old", "new");
    }

    @Test
    void updateTrainerUsername_delegatesToRepository() {
        when(classTrainerRepository.updateTrainerUsername("coach99", "coach100")).thenReturn(Mono.just("Username updated"));

        StepVerifier.create(service.updateTrainerUsername("coach99", "coach100"))
                .expectNext("Username updated")
                .verifyComplete();
    }

    @Test
    void updateEmail_delegatesToRepository() {
        when(classTrainerRepository.updateEmail("coach99", "new@test.com")).thenReturn(Mono.just("Email updated"));

        StepVerifier.create(service.updateEmail("coach99", "new@test.com"))
                .expectNext("Email updated")
                .verifyComplete();
    }

    @Test
    void addSpecialtyToTrainer_delegatesToRepository() {
        when(classTrainerRepository.addSpecialtyToTrainer("coach99", "Yoga")).thenReturn(Mono.just("Specialty added"));

        StepVerifier.create(service.addSpecialtyToTrainer("coach99", "Yoga"))
                .expectNext("Specialty added")
                .verifyComplete();
    }

    @Test
    void dessociateSpecialty_delegatesToRepository() {
        when(classTrainerRepository.dessociateSpecialty("coach99", "Yoga")).thenReturn(Mono.just("Specialty removed"));

        StepVerifier.create(service.dessociateSpecialty("coach99", "Yoga"))
                .expectNext("Specialty removed")
                .verifyComplete();
    }

    @Test
    void addClassToTrainer_delegatesToRepository() {
        when(classTrainerRepository.addClassToTrainer("coach99", "Yoga101")).thenReturn(Mono.just("Class added"));

        StepVerifier.create(service.addClassToTrainer("coach99", "Yoga101"))
                .expectNext("Class added")
                .verifyComplete();
    }

    @Test
    void dessociateClassToTrainer_delegatesToRepository() {
        when(classTrainerRepository.dessociateClassToTrainer("coach99", "Yoga101")).thenReturn(Mono.just("Class removed"));

        StepVerifier.create(service.dessociateClassToTrainer("coach99", "Yoga101"))
                .expectNext("Class removed")
                .verifyComplete();
    }

    @Test
    void deleteClassTrainerByUsernameWithPassword_delegatesToRepository() {
        when(classTrainerRepository.deleteClassTrainerByUsernameWithPassword("coach99", "pw"))
                .thenReturn(Mono.just("Deleted"));

        StepVerifier.create(service.deleteClassTrainerByUsernameWithPassword("coach99", "pw"))
                .expectNext("Deleted")
                .verifyComplete();
    }
}
