package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiReactivePasswordChecker;
import org.springframework.web.reactive.function.client.WebClient;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Properties.ServicesUrl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Every method here builds its own URI by string concatenation on servicesUrl.getInfo().getUrl();
 * these tests capture and assert the actual URI built, since that's exactly the class of bug
 * TESTING_NOTES.md flags for this service (credentials/path-variable mistakes going straight to
 * the wire). Note changePassword still puts oldPassword/newPassword as URL path segments in the
 * outbound call to server-informations - the handler-level fix only moved the client-facing
 * request body off the URL, not this internal hop; captured here as existing behavior, not fixed.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class ClientInformationClientTest {

    @Mock
    private HaveIBeenPwnedRestApiReactivePasswordChecker passwordChecker;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private static final String BASE_URL = "http://server-informations:8082";

    private ClientInformationClient repository;

    @BeforeEach
    void setUp() {
        ServicesUrl servicesUrl = new ServicesUrl();
        ServicesUrl.InfoProperties info = new ServicesUrl.InfoProperties();
        info.setUrl(BASE_URL);
        servicesUrl.setInfo(info);

        repository = new ClientInformationClient(passwordChecker, webClientBuilder, servicesUrl);
        // lenient: the validatePasswordRegister_* tests exercise passwordChecker directly and
        // never touch webClientBuilder/webClient at all.
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
    }

    private AllClient sampleClient() {
        return new AllClient(1L, "jdoe", "pw", "jdoe@test.com", "coach99", "John", "Q", "Doe", "Public",
                "30", "180", "80", "GOLD", LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1), 49.99);
    }

    @Test
    void getAllClients_getsTheFixedAllClientsEndpoint_andCollectsTheFlux() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        AllClient client = sampleClient();
        when(responseSpec.bodyToFlux(AllClient.class)).thenReturn(Flux.just(client));

        StepVerifier.create(repository.getAllClients())
                .expectNext(List.of(client))
                .verifyComplete();

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestHeadersUriSpec).uri(uriCaptor.capture());
        assertThat(uriCaptor.getValue()).isEqualTo(BASE_URL + "/api/clients/AD/allClients");
    }

    @Test
    void updateClient_putsToUpdateBasicInformation_withUsernamePathVariableAndBody() {
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq("jdoe"))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        DtoDetailUserSent dto = new DtoDetailUserSent("John", "Q", "Doe", "Public", "30", "80", "180");
        when(requestBodySpec.bodyValue(dto)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Updated"));

        StepVerifier.create(repository.updateClient("jdoe", dto))
                .expectNext("Updated")
                .verifyComplete();

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestBodyUriSpec).uri(uriCaptor.capture(), eq("jdoe"));
        assertThat(uriCaptor.getValue()).isEqualTo(BASE_URL + "/api/clients/{username}/updateBasicInformation");
    }

    @Test
    void changePassword_putsWithUsernameOldAndNewPasswordAsPathVariables() {
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq("jdoe"), eq("old-pw"), eq("new-pw"))).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Password changed"));

        StepVerifier.create(repository.changePassword("jdoe", "old-pw", "new-pw"))
                .expectNext("Password changed")
                .verifyComplete();

        verify(requestBodyUriSpec).uri(anyString(), eq("jdoe"), eq("old-pw"), eq("new-pw"));
    }

    @Test
    void changeEmail_putsWithUsernameAndEmailAsPathVariables() {
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq("jdoe"), eq("new@test.com"))).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Email changed"));

        StepVerifier.create(repository.changeEmail("jdoe", "new@test.com"))
                .expectNext("Email changed")
                .verifyComplete();
    }

    @Test
    void changeUsername_putsWithOldAndNewUsernameAsPathVariables() {
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq("jdoe"), eq("jdoe2"))).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Username changed"));

        StepVerifier.create(repository.changeUsername("jdoe", "jdoe2"))
                .expectNext("Username changed")
                .verifyComplete();
    }

    @Test
    void changeMembership_putsWithUsernameAndMembershipTypeAsPathVariables() {
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq("jdoe"), eq("GOLD"))).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Membership assigned"));

        StepVerifier.create(repository.changeMembership("jdoe", "GOLD"))
                .expectNext("Membership assigned")
                .verifyComplete();
    }

    @Test
    void unassignMembership_deletesWithUsernameAndMembershipTypeAsPathVariables() {
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("jdoe"), eq("GOLD"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Membership unassigned"));

        StepVerifier.create(repository.unassignMembership("jdoe", "GOLD"))
                .expectNext("Membership unassigned")
                .verifyComplete();
    }

    @Test
    void changeTrainer_putsWithUsernameAndTrainerUsernameAsPathVariables() {
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq("jdoe"), eq("coach99"))).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Trainer assigned"));

        StepVerifier.create(repository.changeTrainer("jdoe", "coach99"))
                .expectNext("Trainer assigned")
                .verifyComplete();
    }

    @Test
    void unassignTrainer_deletesWithUsernameAndTrainerUsernameAsPathVariables() {
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("jdoe"), eq("coach99"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Trainer unassigned"));

        StepVerifier.create(repository.unassignTrainer("jdoe", "coach99"))
                .expectNext("Trainer unassigned")
                .verifyComplete();
    }

    @Test
    void getWorkClassesByClientId_getsWithUsernameAsPathVariable_andCollectsTheFlux() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("jdoe"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        WorkClass workClass = new WorkClass(1L, "Yoga", "Relaxing", "60min");
        when(responseSpec.bodyToFlux(WorkClass.class)).thenReturn(Flux.just(workClass));

        StepVerifier.create(repository.getWorkClassesByClientId("jdoe"))
                .expectNext(List.of(workClass))
                .verifyComplete();
    }

    @Test
    void validateIfUserNameExistsClient_returnsTrue_whenBackendReportsExists() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("jdoe"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));

        StepVerifier.create(repository.validateIfUserNameExistsClient("jdoe"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validateIfEmailExistsClient_returnsFalse_whenBackendReportsNotExists() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("new@test.com"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(false));

        StepVerifier.create(repository.validateIfEmailExistsClient("new@test.com"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void deleteClient_deletesWithUsernameAndPasswordAsPathVariables() {
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("jdoe"), eq("pw"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Deleted"));

        StepVerifier.create(repository.deleteClient("jdoe", "pw"))
                .expectNext("Deleted")
                .verifyComplete();
    }

    @Test
    void createClient_postsToTheFixedCreateEndpoint_withClientAsBody() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        AllClient client = sampleClient();
        when(requestBodySpec.bodyValue(client)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Created"));

        StepVerifier.create(repository.createClient(client))
                .expectNext("Created")
                .verifyComplete();

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestBodyUriSpec).uri(uriCaptor.capture());
        assertThat(uriCaptor.getValue()).isEqualTo(BASE_URL + "/api/clients/AD/create");
    }

    @Test
    void validatePasswordRegister_delegatesToHaveIBeenPwnedChecker_andReturnsCompromisedFlag() {
        CompromisedPasswordDecision decision = mock(CompromisedPasswordDecision.class);
        when(decision.isCompromised()).thenReturn(true);
        when(passwordChecker.check("password123")).thenReturn(Mono.just(decision));

        StepVerifier.create(repository.validatePasswordRegister("password123"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validatePasswordRegister_returnsFalse_whenPasswordIsNotCompromised() {
        CompromisedPasswordDecision decision = mock(CompromisedPasswordDecision.class);
        when(decision.isCompromised()).thenReturn(false);
        when(passwordChecker.check("Un1queP@ssw0rd!")).thenReturn(Mono.just(decision));

        StepVerifier.create(repository.validatePasswordRegister("Un1queP@ssw0rd!"))
                .expectNext(false)
                .verifyComplete();
    }
}
