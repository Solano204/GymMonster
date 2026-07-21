package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import application.Ports.Drivens.InterfaceRepositories.ClientRepositoryInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * RegisterServiceTest already covers createClient's validation chain thoroughly. This covers
 * every OTHER ClientServiceInterface method RegisterService implements - all pure delegation to
 * ClientRepositoryInterface except updateClientPassword, which checks password strength first.
 */
@ExtendWith(MockitoExtension.class)
class RegisterServiceFullCoverageTest {

    @Mock
    private ClientRepositoryInterface clientRepository;

    private RegisterService service;

    @BeforeEach
    void setUp() {
        service = new RegisterService(clientRepository);
    }

    @Test
    void updateClientPassword_rejectsAnInsecureNewPassword_withoutCallingChangePassword() {
        when(clientRepository.validatePasswordRegister("weak")).thenReturn(Mono.just(true));

        StepVerifier.create(service.updateClientPassword("jdoe", "weak", "old-pw"))
                .expectNext("Password is insecure")
                .verifyComplete();

        verify(clientRepository, never()).changePassword(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void updateClientPassword_delegatesToChangePassword_whenNewPasswordIsSecure() {
        when(clientRepository.validatePasswordRegister("S3cur3P@ss!")).thenReturn(Mono.just(false));
        when(clientRepository.changePassword("jdoe", "old-pw", "S3cur3P@ss!")).thenReturn(Mono.just("Password changed"));

        StepVerifier.create(service.updateClientPassword("jdoe", "S3cur3P@ss!", "old-pw"))
                .expectNext("Password changed")
                .verifyComplete();
    }

    @Test
    void updateClientUsername_delegatesToRepository() {
        when(clientRepository.changeUsername("jdoe", "jdoe2")).thenReturn(Mono.just("Username changed"));

        StepVerifier.create(service.updateClientUsername("jdoe", "jdoe2")).expectNext("Username changed").verifyComplete();
    }

    @Test
    void updateClientEmail_delegatesToRepository() {
        when(clientRepository.changeEmail("jdoe", "new@test.com")).thenReturn(Mono.just("Email changed"));

        StepVerifier.create(service.updateClientEmail("jdoe", "new@test.com")).expectNext("Email changed").verifyComplete();
    }

    @Test
    void updateClientMembership_delegatesToRepository() {
        when(clientRepository.changeMembership("jdoe", "GOLD")).thenReturn(Mono.just("Membership changed"));

        StepVerifier.create(service.updateClientMembership("jdoe", "GOLD")).expectNext("Membership changed").verifyComplete();
    }

    @Test
    void updateClientTrainer_delegatesToRepositoryChangeTrainer() {
        when(clientRepository.changeTrainer("jdoe", "coach2")).thenReturn(Mono.just("Trainer changed"));

        StepVerifier.create(service.updateClientTrainer("jdoe", "coach2")).expectNext("Trainer changed").verifyComplete();
        verify(clientRepository).changeTrainer("jdoe", "coach2");
    }

    @Test
    void updateClientAllDetailInformation_delegatesToRepositoryUpdateAllInformation() {
        DtoDetailUserSent dto = new DtoDetailUserSent("John", "Q", "Doe", "Public", "30", "80", "180");
        when(clientRepository.updateAllInformation(dto, "jdoe")).thenReturn(Mono.just("Updated"));

        StepVerifier.create(service.updateClientAllDetailInformation("jdoe", dto)).expectNext("Updated").verifyComplete();
    }

    @Test
    void deleteAccount_delegatesToRepositoryDeleteClient() {
        when(clientRepository.deleteClient("jdoe", "pw")).thenReturn(Mono.just("Deleted"));

        StepVerifier.create(service.deleteAccount("jdoe", "pw")).expectNext("Deleted").verifyComplete();
    }

    @Test
    void getClient_delegatesToRepositoryGetClientData() {
        AllClient allClient = new AllClient(1L, "jdoe", "pw", "jdoe@test.com", "coach99", "John", "Q", "Doe", "Public",
                "30", "180", "80", "GOLD", LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1), 49.99);
        when(clientRepository.getClientData("jdoe")).thenReturn(Mono.just(allClient));

        StepVerifier.create(service.getClient("jdoe")).expectNext(allClient).verifyComplete();
    }

    @Test
    void removeMembership_delegatesToRepositoryUnassignMembership() {
        when(clientRepository.unassignMembership("jdoe", "GOLD")).thenReturn(Mono.just("Membership removed"));

        StepVerifier.create(service.removeMembership("jdoe", "GOLD")).expectNext("Membership removed").verifyComplete();
    }

    @Test
    void removeTrainer_delegatesToRepositoryUnassignTrainer() {
        when(clientRepository.unassignTrainer("jdoe", "coach99")).thenReturn(Mono.just("Trainer removed"));

        StepVerifier.create(service.removeTrainer("jdoe", "coach99")).expectNext("Trainer removed").verifyComplete();
    }

    @Test
    void getWorkClassesByClientId_delegatesToRepositoryGetWorkClassesByClient() {
        List<WorkClass> workClasses = List.of(new WorkClass(1L, "Yoga", "desc", "60min"));
        when(clientRepository.getWorkClassesByClient("jdoe")).thenReturn(Mono.just(workClasses));

        StepVerifier.create(service.getWorkClassesByClientId("jdoe")).expectNext(workClasses).verifyComplete();
    }
}
