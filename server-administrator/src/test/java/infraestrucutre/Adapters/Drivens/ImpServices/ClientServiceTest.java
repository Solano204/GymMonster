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

import application.Ports.Drivens.RepositoriesInterfaces.ClientInformationClientInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * ClientService is a pure delegation layer over ClientInformationClientInterface - these
 * tests pin that every ClientServiceInterface method forwards to the right repository method
 * with the right arguments (and, for removeMembership/removeTrainer, the right *renamed*
 * repository method - unassignMembership/unassignTrainer - which is an easy copy-paste slip).
 */
@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientInformationClientInterface clientRepository;

    private ClientService service;

    @BeforeEach
    void setUp() {
        service = new ClientService(clientRepository);
    }

    private AllClient sampleClient() {
        return new AllClient(1L, "jdoe", "pw", "jdoe@test.com", "coach99", "John", "Q", "Doe", "Public",
                "30", "180", "80", "GOLD", LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1), 49.99);
    }

    @Test
    void getAllClients_delegatesToRepository() {
        List<AllClient> clients = List.of(sampleClient());
        when(clientRepository.getAllClients()).thenReturn(Mono.just(clients));

        StepVerifier.create(service.getAllClients()).expectNext(clients).verifyComplete();
    }

    @Test
    void getWorkClassesByClientId_delegatesToRepository() {
        List<WorkClass> workClasses = List.of(new WorkClass(1L, "Yoga", "desc", "60min"));
        when(clientRepository.getWorkClassesByClientId("jdoe")).thenReturn(Mono.just(workClasses));

        StepVerifier.create(service.getWorkClassesByClientId("jdoe")).expectNext(workClasses).verifyComplete();
    }

    @Test
    void deleteClient_delegatesToRepositoryWithUsernameAndPassword() {
        when(clientRepository.deleteClient("jdoe", "pw")).thenReturn(Mono.just("Deleted"));

        StepVerifier.create(service.deleteClient("jdoe", "pw")).expectNext("Deleted").verifyComplete();
        verify(clientRepository).deleteClient("jdoe", "pw");
    }

    @Test
    void createClient_delegatesToRepository() {
        AllClient client = sampleClient();
        when(clientRepository.createClient(client)).thenReturn(Mono.just("Created"));

        StepVerifier.create(service.createClient(client)).expectNext("Created").verifyComplete();
    }

    @Test
    void validateIfUserNameExistsClient_delegatesToRepository() {
        when(clientRepository.validateIfUserNameExistsClient("jdoe")).thenReturn(Mono.just(true));

        StepVerifier.create(service.validateIfUserNameExistsClient("jdoe")).expectNext(true).verifyComplete();
    }

    @Test
    void validateIfEmailExistsClient_delegatesToRepository() {
        when(clientRepository.validateIfEmailExistsClient("jdoe@test.com")).thenReturn(Mono.just(false));

        StepVerifier.create(service.validateIfEmailExistsClient("jdoe@test.com")).expectNext(false).verifyComplete();
    }

    @Test
    void changePassword_delegatesToRepositoryWithAllThreeArguments() {
        when(clientRepository.changePassword("jdoe", "old", "new")).thenReturn(Mono.just("Password changed"));

        StepVerifier.create(service.changePassword("jdoe", "old", "new"))
                .expectNext("Password changed")
                .verifyComplete();
        verify(clientRepository).changePassword("jdoe", "old", "new");
    }

    @Test
    void changeEmail_delegatesToRepository() {
        when(clientRepository.changeEmail("jdoe", "new@test.com")).thenReturn(Mono.just("Email changed"));

        StepVerifier.create(service.changeEmail("jdoe", "new@test.com"))
                .expectNext("Email changed")
                .verifyComplete();
    }

    @Test
    void changeUsername_delegatesToRepository() {
        when(clientRepository.changeUsername("jdoe", "jdoe2")).thenReturn(Mono.just("Username changed"));

        StepVerifier.create(service.changeUsername("jdoe", "jdoe2"))
                .expectNext("Username changed")
                .verifyComplete();
    }

    @Test
    void updateAllInformation_delegatesToRepositoryUpdateClient_withArgumentsInRepositoryOrder() {
        DtoDetailUserSent dto = new DtoDetailUserSent("John", "Q", "Doe", "Public", "30", "80", "180");
        when(clientRepository.updateClient("jdoe", dto)).thenReturn(Mono.just("Updated"));

        StepVerifier.create(service.updateAllInformation(dto, "jdoe"))
                .expectNext("Updated")
                .verifyComplete();
        verify(clientRepository).updateClient("jdoe", dto);
    }

    @Test
    void validatePasswordRegister_delegatesToRepository() {
        when(clientRepository.validatePasswordRegister("pw")).thenReturn(Mono.just(true));

        StepVerifier.create(service.validatePasswordRegister("pw")).expectNext(true).verifyComplete();
    }

    @Test
    void changeMembership_delegatesToRepository() {
        when(clientRepository.changeMembership("jdoe", "GOLD")).thenReturn(Mono.just("Membership changed"));

        StepVerifier.create(service.changeMembership("jdoe", "GOLD"))
                .expectNext("Membership changed")
                .verifyComplete();
    }

    @Test
    void changeTrainer_delegatesToRepository() {
        when(clientRepository.changeTrainer("jdoe", "coach99")).thenReturn(Mono.just("Trainer changed"));

        StepVerifier.create(service.changeTrainer("jdoe", "coach99"))
                .expectNext("Trainer changed")
                .verifyComplete();
    }

    @Test
    void removeMembership_delegatesToRepositoryUnassignMembership() {
        when(clientRepository.unassignMembership("jdoe", "GOLD")).thenReturn(Mono.just("Membership removed"));

        StepVerifier.create(service.removeMembership("jdoe", "GOLD"))
                .expectNext("Membership removed")
                .verifyComplete();
        verify(clientRepository).unassignMembership("jdoe", "GOLD");
    }

    @Test
    void removeTrainer_delegatesToRepositoryUnassignTrainer() {
        when(clientRepository.unassignTrainer("jdoe", "coach99")).thenReturn(Mono.just("Trainer removed"));

        StepVerifier.create(service.removeTrainer("jdoe", "coach99"))
                .expectNext("Trainer removed")
                .verifyComplete();
        verify(clientRepository).unassignTrainer("jdoe", "coach99");
    }
}
