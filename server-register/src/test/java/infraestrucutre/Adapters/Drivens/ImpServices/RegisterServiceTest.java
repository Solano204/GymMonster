package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import infraestrucutre.Adapters.Drivens.DTOS.DtoKeyCloakUser;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.Client;
import infraestrucutre.Adapters.Drivens.Entities.DetailUser;
import infraestrucutre.Adapters.Drivens.Entities.Membership;
import infraestrucutre.Adapters.Drivens.Entities.PerTrainer;
import infraestrucutre.Adapters.Drivens.IServices.IEmailService;
import infraestrucutre.Adapters.Drivens.Repositories.ClientRepository;
import infraestrucutre.Adapters.Drivens.Repositories.DetailClientRepository;
import infraestrucutre.Adapters.Drivens.Repositories.InscriptionRepository;
import infraestrucutre.Adapters.Drivens.Repositories.MembershipRepository;
import infraestrucutre.Adapters.Drivens.Repositories.PerTrainerRepository;
import infraestrucutre.Adapters.Drivens.Validations.LogicInterfaces.ValidatePassword;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * RegisterService.createClient is the heart of the registration flow: it's what
 * server-register's Kafka listener calls for every event web-page publishes. Covers the
 * validation short-circuits (each returns before touching any repository) and the full
 * happy-path save chain (detail -> client -> inscription -> Keycloak -> email).
 */
class RegisterServiceTest {

    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ClientRepository clientRepository;
    @Mock private MembershipRepository membershipRepository;
    @Mock private DetailClientRepository detailClientRepository;
    @Mock private PerTrainerRepository perTrainerRepository;
    @Mock private ValidatePassword validatePasswordRegister;
    @Mock private InscriptionRepository inscriptionRepository;
    @Mock private KeycloakServiceImpl keycloakService;
    @Mock private IEmailService emailService;

    private RegisterService registerService;

    private AllClient validClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registerService = new RegisterService(
                passwordEncoder,
                clientRepository,
                membershipRepository,
                detailClientRepository,
                perTrainerRepository,
                validatePasswordRegister,
                inscriptionRepository,
                keycloakService,
                new FillinKeycloakUser(),
                emailService);

        validClient = new AllClient(
                null, "newuser", "Str0ngPassword!", "newuser@example.com", "trainer1",
                "Carlos", "Josue", "Lopez", "Solano", "30", "180", "75",
                "premium", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1),
                LocalDate.of(2025, 1, 1), 99.99);
    }

    @Test
    void rejectsRegistrationWhenUsernameAlreadyExists() {
        when(clientRepository.existsByUsername(validClient.username())).thenReturn(Mono.just(true));
        when(clientRepository.existsByEmail(validClient.email())).thenReturn(Mono.just(false));

        StepVerifier.create(registerService.createClient(validClient))
                .assertNext(errors -> assertThat(errors)
                        .anyMatch(e -> e.contains("username already exists")))
                .verifyComplete();

        verify(detailClientRepository, never()).save(any());
        verify(keycloakService, never()).createUser(any());
    }

    @Test
    void rejectsRegistrationWhenEmailAlreadyExists() {
        when(clientRepository.existsByUsername(validClient.username())).thenReturn(Mono.just(false));
        when(clientRepository.existsByEmail(validClient.email())).thenReturn(Mono.just(true));

        StepVerifier.create(registerService.createClient(validClient))
                .assertNext(errors -> assertThat(errors).anyMatch(e -> e.contains("email already exists")))
                .verifyComplete();

        verify(detailClientRepository, never()).save(any());
    }

    @Test
    void rejectsRegistrationWhenBasicFieldsAreInvalid() {
        AllClient missingName = new AllClient(
                null, "newuser", "Str0ngPassword!", "newuser@example.com", "trainer1",
                "", "Josue", "Lopez", "Solano", "30", "180", "75",
                "premium", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1),
                LocalDate.of(2025, 1, 1), 99.99);
        when(clientRepository.existsByUsername(anyString())).thenReturn(Mono.just(false));
        when(clientRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));

        StepVerifier.create(registerService.createClient(missingName))
                .assertNext(errors -> assertThat(errors)
                        .anyMatch(e -> e.contains("first name cannot be empty")))
                .verifyComplete();

        verify(detailClientRepository, never()).save(any());
    }

    @Test
    void rejectsRegistrationWhenSameDetailInfoAlreadyRegistered() {
        when(clientRepository.existsByUsername(anyString())).thenReturn(Mono.just(false));
        when(clientRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));
        when(detailClientRepository.countByDetailInfo("Carlos", "Josue", "Solano", "Lopez"))
                .thenReturn(Mono.just(1));

        StepVerifier.create(registerService.createClient(validClient))
                .assertNext(errors -> assertThat(errors).anyMatch(e -> e.contains("already exists with that data")))
                .verifyComplete();

        verify(detailClientRepository, never()).save(any());
    }

    @Test
    void happyPathSavesEverythingThenProvisionsKeycloakAndSendsEmail() {
        when(clientRepository.existsByUsername(anyString())).thenReturn(Mono.just(false));
        when(clientRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));
        when(detailClientRepository.countByDetailInfo("Carlos", "Josue", "Solano", "Lopez"))
                .thenReturn(Mono.just(0));

        DetailUser savedDetail = new DetailUser();
        savedDetail.setId(10L);
        when(detailClientRepository.save(any(DetailUser.class))).thenReturn(Mono.just(savedDetail));

        Membership membership = new Membership(1L, "premium", "Premium plan", true, true, true);
        when(membershipRepository.findByMembershipType("premium")).thenReturn(Mono.just(membership));

        PerTrainer trainer = new PerTrainer();
        trainer.setId(2L);
        when(perTrainerRepository.findByUsername("trainer1")).thenReturn(Mono.just(trainer));

        when(passwordEncoder.encode(validClient.password())).thenReturn("encoded-password");

        Client savedClient = new Client();
        savedClient.setId(99L);
        when(clientRepository.save(any(Client.class))).thenReturn(Mono.just(savedClient));

        when(inscriptionRepository.save(any())).thenReturn(Mono.empty());
        when(keycloakService.createUser(any(DtoKeyCloakUser.class))).thenReturn(Mono.empty());
        when(emailService.sendEmail(any(), anyString(), anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(registerService.createClient(validClient))
                .assertNext(messages -> assertThat(messages)
                        .anyMatch(m -> m.contains("confirmation email")))
                .verifyComplete();

        verify(clientRepository, times(1)).save(any(Client.class));
        verify(keycloakService, times(1)).createUser(any(DtoKeyCloakUser.class));
        verify(emailService, times(1)).sendEmail(any(), org.mockito.ArgumentMatchers.eq(validClient.username()),
                org.mockito.ArgumentMatchers.eq(validClient.password()));
    }

    @Test
    void downstreamFailureIsCaughtAndReportedAsAnError() {
        when(clientRepository.existsByUsername(anyString())).thenReturn(Mono.just(false));
        when(clientRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));
        when(detailClientRepository.countByDetailInfo("Carlos", "Josue", "Solano", "Lopez"))
                .thenReturn(Mono.just(0));
        when(detailClientRepository.save(any(DetailUser.class)))
                .thenReturn(Mono.error(new RuntimeException("DB connection lost")));

        StepVerifier.create(registerService.createClient(validClient))
                .assertNext(errors -> assertThat(errors).anyMatch(e -> e.contains("An error occurred")))
                .verifyComplete();
    }
}
