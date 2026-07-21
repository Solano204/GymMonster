package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
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

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class ClientRepositoryTest {

    @Mock
    private HaveIBeenPwnedRestApiReactivePasswordChecker passwordChecker;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private KafkaTemplate<String, AllClient> postmanRegistration;

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

    private ClientRepository repository;

    @BeforeEach
    void setUp() {
        ServicesUrl servicesUrl = new ServicesUrl();
        ServicesUrl.InfoProperties info = new ServicesUrl.InfoProperties();
        info.setUrl(BASE_URL);
        servicesUrl.setInfo(info);

        repository = new ClientRepository(passwordChecker, webClientBuilder, postmanRegistration, servicesUrl);
    }

    private void mockWebClientBuild() {
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    private AllClient sampleClient() {
        return new AllClient(1L, "jdoe", "pw", "jdoe@test.com", "coach99", "John", "Q", "Doe", "Public",
                "30", "180", "80", "GOLD", LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1), 49.99);
    }

    @Test
    void registerNewClient_publishesToTheFlowKafkaTopic() {
        AllClient client = sampleClient();

        StepVerifier.create(repository.registerNewClient(client))
                .assertNext(message -> assertThat(message).contains("being processed"))
                .verifyComplete();

        verify(postmanRegistration).send("flow", client);
    }

    @Test
    void registerNewClient_wrapsKafkaSendFailureAsAMonoError() {
        AllClient client = sampleClient();
        org.mockito.Mockito.doThrow(new RuntimeException("Broker unreachable"))
                .when(postmanRegistration).send(anyString(), org.mockito.ArgumentMatchers.any(AllClient.class));

        StepVerifier.create(repository.registerNewClient(client))
                .expectErrorMatches(e -> e.getMessage().contains("Failed to register new client"))
                .verify();
    }

    @Test
    void unassignTrainer_deletesWithUsernameAndTrainerUsernameAsPathVariables() {
        mockWebClientBuild();
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
    void getWorkClassesByClient_getsWithUsernamePathVariable_asACollectedList() {
        mockWebClientBuild();
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("jdoe"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        WorkClass workClass = new WorkClass(1L, "Yoga", "desc", "60min");
        when(responseSpec.bodyToFlux(WorkClass.class)).thenReturn(Flux.just(workClass));

        StepVerifier.create(repository.getWorkClassesByClient("jdoe"))
                .expectNext(java.util.List.of(workClass))
                .verifyComplete();
    }

    @Test
    void getClientData_getsWithUsernamePathVariable() {
        mockWebClientBuild();
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("jdoe"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        AllClient client = sampleClient();
        when(responseSpec.bodyToMono(AllClient.class)).thenReturn(Mono.just(client));

        StepVerifier.create(repository.getClientData("jdoe")).expectNext(client).verifyComplete();
    }

    @Test
    void unassignMembership_deletesWithUsernameAndMembershipTypeAsPathVariables() {
        mockWebClientBuild();
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
    void deleteClient_deletesWithUsernameAndPasswordAsPathVariables() {
        mockWebClientBuild();
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("jdoe"), eq("pw"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Deleted"));

        StepVerifier.create(repository.deleteClient("jdoe", "pw")).expectNext("Deleted").verifyComplete();
    }

    @Test
    void createClient_postsToTheFixedCreateEndpoint_withClientAsBody() {
        mockWebClientBuild();
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        AllClient client = sampleClient();
        when(requestBodySpec.bodyValue(client)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Created"));

        StepVerifier.create(repository.createClient(client)).expectNext("Created").verifyComplete();

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestBodyUriSpec).uri(uriCaptor.capture());
        assertThat(uriCaptor.getValue()).isEqualTo(BASE_URL + "/api/clients/AD/create");
    }

    @Test
    void validateIfUserNameExistsClient_returnsBooleanFromDownstream() {
        mockWebClientBuild();
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("jdoe"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));

        StepVerifier.create(repository.validateIfUserNameExistsClient("jdoe")).expectNext(true).verifyComplete();
    }

    @Test
    void validateIfEmailExistsClient_returnsBooleanFromDownstream() {
        mockWebClientBuild();
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("jdoe@test.com"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(false));

        StepVerifier.create(repository.validateIfEmailExistsClient("jdoe@test.com")).expectNext(false).verifyComplete();
    }

    @Test
    void validateOldPasswordMatchers_putsWithUsernameOldPasswordAndPasswordAsPathVariables() {
        mockWebClientBuild();
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq("jdoe"), eq("pw"))).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));

        StepVerifier.create(repository.validateOldPasswordMatchers("jdoe", "pw")).expectNext(true).verifyComplete();
    }

    @Test
    void changePassword_putsWithUsernameOldAndNewPasswordAsPathVariables() {
        mockWebClientBuild();
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq("jdoe"), eq("old-pw"), eq("new-pw"))).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Password changed"));

        StepVerifier.create(repository.changePassword("jdoe", "old-pw", "new-pw"))
                .expectNext("Password changed")
                .verifyComplete();
    }

    @Test
    void changeEmail_putsWithUsernameAndEmailAsPathVariables() {
        mockWebClientBuild();
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
    void updateAllInformation_putsWithUsernamePathVariable_andBody() {
        mockWebClientBuild();
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq("jdoe"))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        DtoDetailUserSent dto = new DtoDetailUserSent("John", "Q", "Doe", "Public", "30", "80", "180");
        when(requestBodySpec.bodyValue(dto)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Updated"));

        StepVerifier.create(repository.updateAllInformation(dto, "jdoe")).expectNext("Updated").verifyComplete();
    }

    @Test
    void validatePasswordRegister_delegatesToHaveIBeenPwnedChecker() {
        CompromisedPasswordDecision decision = mock(CompromisedPasswordDecision.class);
        when(decision.isCompromised()).thenReturn(true);
        when(passwordChecker.check("password123")).thenReturn(Mono.just(decision));

        StepVerifier.create(repository.validatePasswordRegister("password123")).expectNext(true).verifyComplete();
    }

    @Test
    void changeUsername_putsWithOldAndNewUsernameAsPathVariables() {
        mockWebClientBuild();
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
        mockWebClientBuild();
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
    void changeTrainer_putsWithUsernameAndTrainerUsernameAsPathVariables() {
        mockWebClientBuild();
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
    void updateClient_withAllClientBody_putsWithUsernamePathVariable() {
        mockWebClientBuild();
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq("jdoe"))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        AllClient client = sampleClient();
        when(requestBodySpec.bodyValue(client)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Updated"));

        StepVerifier.create(repository.updateClient("jdoe", client)).expectNext("Updated").verifyComplete();
    }
}
