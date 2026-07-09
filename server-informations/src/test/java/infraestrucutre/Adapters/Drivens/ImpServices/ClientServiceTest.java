package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import application.Ports.Drivers.IServices.IEmailService;
import infraestrucutre.Adapters.Drivens.Entities.Client;
import infraestrucutre.Adapters.Drivens.Repositories.ClientRepository;
import infraestrucutre.Adapters.Drivens.Repositories.DetailClientRepository;
import infraestrucutre.Adapters.Drivens.Repositories.InscriptionRepository;
import infraestrucutre.Adapters.Drivens.Repositories.MembershipRepository;
import infraestrucutre.Adapters.Drivens.Repositories.PerTrainerRepository;
import infraestrucutre.Adapters.Drivens.Validations.LogicInterfaces.ValidatePassword;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * ClientService is server-informations' largest and most security-sensitive class: every
 * password/username/email change goes through a "does the old value match" check before
 * writing anything and syncing Keycloak. Focuses on the password-verification paths
 * (deleteClient, updateClientPassword, updateClientUsername) since a bug there is the
 * highest-risk kind (auth bypass or account lockout), rather than the more repetitive
 * membership/trainer assignment methods that follow the same already-covered pattern.
 */
class ClientServiceTest {

    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ClientRepository clientRepository;
    @Mock private MembershipRepository membershipRepository;
    @Mock private DetailClientRepository detailClientRepository;
    @Mock private PerTrainerRepository perTrainerRepository;
    @Mock private ValidatePassword validatePasswordRegister;
    @Mock private InscriptionRepository inscriptionRepository;
    @Mock private KeycloakServiceImpl keycloakService;
    @Mock private IEmailService emailService;

    private ClientService clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clientService = new ClientService(
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

        // ClientService chains several calls via .then(repo.method(...)), whose argument
        // is evaluated eagerly when the chain is assembled - even in tests where an
        // earlier step is expected to fail before actually reaching them. Default them to
        // non-null Monos here; tests that care about a specific outcome override below.
        when(clientRepository.deleteById(any(Long.class))).thenReturn(Mono.empty());
        when(detailClientRepository.deleteById(any(Long.class))).thenReturn(Mono.empty());
        when(keycloakService.deleteUser(anyString())).thenReturn(Mono.just("deleted"));
    }

    private Client existingClient() {
        Client client = new Client();
        client.setId(1L);
        client.setId_detail(2L);
        client.setUsername("existinguser");
        client.setPassword("encoded-old-password");
        client.setEmail("existing@example.com");
        return client;
    }

    // ---- deleteClient ----

    @Test
    void deleteClientReportsWhenUsernameDoesNotExist() {
        when(clientRepository.existsByUsername("ghost")).thenReturn(Mono.just(false));

        StepVerifier.create(clientService.deleteClient("ghost", "any-password"))
                .expectNext("Username does not exist")
                .verifyComplete();

        verify_neverDeletes();
    }

    @Test
    void deleteClientRejectsAWrongPassword() {
        Client client = existingClient();
        when(clientRepository.existsByUsername(client.getUsername())).thenReturn(Mono.just(true));
        when(clientRepository.findByUsername(client.getUsername())).thenReturn(Mono.just(client));
        when(passwordEncoder.matches("wrong-password", client.getPassword())).thenReturn(false);

        StepVerifier.create(clientService.deleteClient(client.getUsername(), "wrong-password"))
                .expectNext("Old password is not correct")
                .verifyComplete();

        verify_neverDeletes();
    }

    @Test
    void deleteClientCascadesThroughInscriptionDetailAndKeycloakOnCorrectPassword() {
        Client client = existingClient();
        when(clientRepository.existsByUsername(client.getUsername())).thenReturn(Mono.just(true));
        when(clientRepository.findByUsername(client.getUsername())).thenReturn(Mono.just(client));
        when(passwordEncoder.matches("correct-password", client.getPassword())).thenReturn(true);
        when(inscriptionRepository.deleteByClientId(client.getId())).thenReturn(Mono.empty());
        when(clientRepository.deleteById(client.getId())).thenReturn(Mono.empty());
        when(detailClientRepository.deleteById(client.getId_detail())).thenReturn(Mono.empty());
        when(keycloakService.deleteUser(client.getUsername())).thenReturn(Mono.just("deleted"));

        StepVerifier.create(clientService.deleteClient(client.getUsername(), "correct-password"))
                .expectNext("Account deleted successfully")
                .verifyComplete();
    }

    @Test
    void deleteClientReportsCascadeFailuresInsteadOfThrowing() {
        Client client = existingClient();
        when(clientRepository.existsByUsername(client.getUsername())).thenReturn(Mono.just(true));
        when(clientRepository.findByUsername(client.getUsername())).thenReturn(Mono.just(client));
        when(passwordEncoder.matches("correct-password", client.getPassword())).thenReturn(true);
        when(inscriptionRepository.deleteByClientId(client.getId()))
                .thenReturn(Mono.error(new RuntimeException("DB unavailable")));

        StepVerifier.create(clientService.deleteClient(client.getUsername(), "correct-password"))
                .expectNextMatches(message -> message.startsWith("Error occurred during deletion:"))
                .verifyComplete();
    }

    private void verify_neverDeletes() {
        org.mockito.Mockito.verify(clientRepository, never()).deleteById(any(Long.class));
        org.mockito.Mockito.verify(keycloakService, never()).deleteUser(anyString());
    }

    // ---- updateClientPassword ----

    @Test
    void updateClientPasswordReportsWhenUsernameDoesNotExist() {
        when(clientRepository.existsByUsername("ghost")).thenReturn(Mono.just(false));

        StepVerifier.create(clientService.updateClientPassword("ghost", "newPass", "oldPass"))
                .expectNext("Username does not exist")
                .verifyComplete();
    }

    @Test
    void updateClientPasswordRejectsAWrongOldPassword() {
        Client client = existingClient();
        when(clientRepository.existsByUsername(client.getUsername())).thenReturn(Mono.just(true));
        when(clientRepository.findByUsername(client.getUsername())).thenReturn(Mono.just(client));
        when(passwordEncoder.matches("wrong-old-password", client.getPassword())).thenReturn(false);

        StepVerifier.create(clientService.updateClientPassword(client.getUsername(), "newPass", "wrong-old-password"))
                .expectNext("Old password is not correct")
                .verifyComplete();

        org.mockito.Mockito.verify(clientRepository, never()).save(any());
    }

    @Test
    void updateClientPasswordSavesEncodedPasswordAndSyncsKeycloak() {
        Client client = existingClient();
        when(clientRepository.existsByUsername(client.getUsername())).thenReturn(Mono.just(true));
        when(clientRepository.findByUsername(client.getUsername())).thenReturn(Mono.just(client));
        when(passwordEncoder.matches("correct-old-password", client.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encoded-new-password");
        when(clientRepository.save(client)).thenReturn(Mono.just(client));
        when(keycloakService.changePassword(client.getUsername(), "newPass")).thenReturn(Mono.just("ok"));

        StepVerifier.create(clientService.updateClientPassword(client.getUsername(), "newPass", "correct-old-password"))
                .expectNext("Password updated successfully")
                .verifyComplete();

        assertThat(client.getPassword()).isEqualTo("encoded-new-password");
    }

    // ---- updateClientUsername ----

    @Test
    void updateClientUsernameRejectsWhenNewUsernameAlreadyTaken() {
        when(clientRepository.existsByUsername("olduser")).thenReturn(Mono.just(true));
        when(clientRepository.existsByUsername("takenuser")).thenReturn(Mono.just(true));

        StepVerifier.create(clientService.updateClientUsername("olduser", "takenuser", "any-password"))
                .expectNext("A user already exists with that new username")
                .verifyComplete();

        org.mockito.Mockito.verify(clientRepository, never()).save(any());
    }

    @Test
    void updateClientUsernameRejectsAWrongPassword() {
        Client client = existingClient();
        when(clientRepository.existsByUsername("existinguser")).thenReturn(Mono.just(true));
        when(clientRepository.existsByUsername("newuser")).thenReturn(Mono.just(false));
        when(clientRepository.findByUsername("existinguser")).thenReturn(Mono.just(client));
        when(passwordEncoder.matches("wrong-password", client.getPassword())).thenReturn(false);

        StepVerifier.create(clientService.updateClientUsername("existinguser", "newuser", "wrong-password"))
                .expectNext("Password does not match")
                .verifyComplete();

        org.mockito.Mockito.verify(clientRepository, never()).save(any());
    }

    @Test
    void updateClientUsernameSucceedsAndSyncsKeycloak() {
        Client client = existingClient();
        when(clientRepository.existsByUsername("existinguser")).thenReturn(Mono.just(true));
        when(clientRepository.existsByUsername("newuser")).thenReturn(Mono.just(false));
        when(clientRepository.findByUsername("existinguser")).thenReturn(Mono.just(client));
        when(passwordEncoder.matches("correct-password", client.getPassword())).thenReturn(true);
        when(clientRepository.save(client)).thenReturn(Mono.just(client));
        when(keycloakService.updateUsername2("newuser", "existinguser", "correct-password", "Client"))
                .thenReturn(Mono.empty());

        StepVerifier.create(clientService.updateClientUsername("existinguser", "newuser", "correct-password"))
                .expectNext("Username updated successfully")
                .verifyComplete();

        assertThat(client.getUsername()).isEqualTo("newuser");
    }
}
