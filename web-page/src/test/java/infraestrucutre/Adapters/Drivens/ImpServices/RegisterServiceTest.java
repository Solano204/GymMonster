package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import application.Ports.Drivens.InterfaceRepositories.ClientRepositoryInterface;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * RegisterService.createClient is the public-facing entry point that validates a
 * registration request before web-page's ClientRepository adapter publishes it to
 * Kafka for server-register to pick up. Had zero coverage. Covers each validation
 * short-circuit and confirms downstream (Kafka-publishing) code is only reached once
 * every check passes.
 */
class RegisterServiceTest {

    @Mock
    private ClientRepositoryInterface clientRepository;

    private RegisterService registerService;

    private AllClient validClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registerService = new RegisterService(clientRepository);

        validClient = new AllClient(
                null, "newuser", "Str0ngPassword!", "newuser@example.com", "trainer1",
                "Carlos", "Josue", "Lopez", "Solano", "30", "180", "75",
                "premium", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1),
                LocalDate.of(2025, 1, 1), 99.99);

        when(clientRepository.validateIfUserNameExistsClient(anyString())).thenReturn(Mono.just(false));
        when(clientRepository.validateIfEmailExistsClient(anyString())).thenReturn(Mono.just(false));
        when(clientRepository.validatePasswordRegister(anyString())).thenReturn(Mono.just(false));
    }

    @Test
    void rejectsRegistrationWhenUsernameAlreadyExists() {
        when(clientRepository.validateIfUserNameExistsClient(validClient.username())).thenReturn(Mono.just(true));

        StepVerifier.create(registerService.createClient(validClient))
                .assertNext(errors -> assertThat(errors).contains("The username already exists"))
                .verifyComplete();

        verify(clientRepository, never()).registerNewClient(any());
    }

    @Test
    void rejectsRegistrationWhenEmailAlreadyExists() {
        when(clientRepository.validateIfEmailExistsClient(validClient.email())).thenReturn(Mono.just(true));

        StepVerifier.create(registerService.createClient(validClient))
                .assertNext(errors -> assertThat(errors).contains("The email already exists"))
                .verifyComplete();

        verify(clientRepository, never()).registerNewClient(any());
    }

    @Test
    void rejectsRegistrationWhenPasswordIsInsecure() {
        when(clientRepository.validatePasswordRegister(validClient.password())).thenReturn(Mono.just(true));

        StepVerifier.create(registerService.createClient(validClient))
                .assertNext(errors -> assertThat(errors).contains("The password is insecure"))
                .verifyComplete();

        verify(clientRepository, never()).registerNewClient(any());
    }

    @Test
    void rejectsRegistrationWhenBasicFieldsAreInvalid() {
        AllClient missingMembership = new AllClient(
                null, "newuser", "Str0ngPassword!", "newuser@example.com", "trainer1",
                "Carlos", "Josue", "Lopez", "Solano", "30", "180", "75",
                "", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1),
                LocalDate.of(2025, 1, 1), 99.99);

        StepVerifier.create(registerService.createClient(missingMembership))
                .assertNext(errors -> assertThat(errors)
                        .anyMatch(e -> e.contains("type of membership cannot be empty")))
                .verifyComplete();

        verify(clientRepository, never()).registerNewClient(any());
    }

    @Test
    void happyPathPublishesTheRegistrationOnceAllValidationsPass() {
        when(clientRepository.registerNewClient(validClient))
                .thenReturn(Mono.just("Registration event published"));

        StepVerifier.create(registerService.createClient(validClient))
                .assertNext(messages -> assertThat(messages).containsExactly("Registration event published"))
                .verifyComplete();

        verify(clientRepository, times(1)).registerNewClient(validClient);
    }

    @Test
    void downstreamFailureIsCaughtAndReportedAsAnError() {
        when(clientRepository.registerNewClient(validClient))
                .thenReturn(Mono.error(new RuntimeException("Kafka broker unreachable")));

        StepVerifier.create(registerService.createClient(validClient))
                .assertNext(errors -> assertThat(errors).anyMatch(e -> e.contains("An error occurred")))
                .verifyComplete();
    }
}
