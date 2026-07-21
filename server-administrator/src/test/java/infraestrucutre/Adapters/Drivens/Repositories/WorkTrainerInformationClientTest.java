package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiReactivePasswordChecker;
import org.springframework.web.reactive.function.client.WebClient;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Properties.ServicesUrl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class WorkTrainerInformationClientTest {

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

    private WorkTrainerInformationClient repository;

    @BeforeEach
    void setUp() {
        ServicesUrl servicesUrl = new ServicesUrl();
        ServicesUrl.InfoProperties info = new ServicesUrl.InfoProperties();
        info.setUrl(BASE_URL);
        servicesUrl.setInfo(info);

        repository = new WorkTrainerInformationClient(passwordChecker, webClientBuilder, servicesUrl);
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    private AllTrainer sampleTrainer() {
        return new AllTrainer(1L, "coach99", "pw", "coach@test.com", "Ana", "M", "Lopez", "Diaz",
                "35", "170", "65", LocalDate.now());
    }

    @Test
    void createClassTrainer_postsToFixedEndpoint_withBody() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        AllTrainer trainer = sampleTrainer();
        when(requestBodySpec.bodyValue(trainer)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Created"));

        StepVerifier.create(repository.createClassTrainer(trainer)).expectNext("Created").verifyComplete();

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestBodyUriSpec).uri(uriCaptor.capture());
        assertThat(uriCaptor.getValue()).isEqualTo(BASE_URL + "/trainers/AD/create");
    }

    @Test
    void getAllTrainers_getsWithPageAndSizeAsPathVariables() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq(0), eq(10))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        DtoTrainerData trainerData = new DtoTrainerData(1L, "coach99", "coach@test.com", "Ana", "M", "Lopez",
                "Diaz", "35", "170", "65", LocalDate.now(), 5);
        when(responseSpec.bodyToFlux(DtoTrainerData.class)).thenReturn(Flux.just(trainerData));

        StepVerifier.create(repository.getAllTrainers(0, 10)).expectNext(trainerData).verifyComplete();
    }

    @Test
    void getAllSpecialtiesFromTrainer_getsWithUsernamePathVariable_asAParameterizedList() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("coach99"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        List<DtoSpecialtyRecived> specialties = List.of(new DtoSpecialtyRecived("Yoga", "desc"));
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(specialties));

        StepVerifier.create(repository.getAllSpecialtiesFromTrainer("coach99"))
                .expectNext(specialties)
                .verifyComplete();
    }

    @Test
    void updateTrainerAllDetailInformation_putsWithUsernamePathVariable_andBody() {
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq("coach99"))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        DtoDetailUserReciving details = new DtoDetailUserReciving("Ana", "M", "Lopez", "Diaz", "35", "65", "170");
        when(requestBodySpec.bodyValue(details)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Updated"));

        StepVerifier.create(repository.updateTrainerAllDetailInformation("coach99", details))
                .expectNext("Updated")
                .verifyComplete();
    }

    @Test
    void getWorkClassesByTrainer_getsWithUsernamePathVariable_asAParameterizedList() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("coach99"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        List<WorkClass> workClasses = List.of(new WorkClass(1L, "Yoga", "desc", "60min"));
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(workClasses));

        StepVerifier.create(repository.getWorkClassesByTrainer("coach99"))
                .expectNext(workClasses)
                .verifyComplete();
    }

    @Test
    void updateTrainerPassword_putsWithUsernameOldAndNewPasswordAsPathVariables() {
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq("coach99"), eq("old-pw"), eq("new-pw"))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Password updated"));

        StepVerifier.create(repository.updateTrainerPassword("coach99", "old-pw", "new-pw"))
                .expectNext("Password updated")
                .verifyComplete();
    }

    @Test
    void updateTrainerUsername_putsWithOldAndNewUsernameAsPathVariables() {
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq("coach99"), eq("coach100"))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Username updated"));

        StepVerifier.create(repository.updateTrainerUsername("coach99", "coach100"))
                .expectNext("Username updated")
                .verifyComplete();
    }

    @Test
    void updateEmail_putsWithUsernameAndNewEmailAsPathVariables() {
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq("coach99"), eq("new@test.com"))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Email updated"));

        StepVerifier.create(repository.updateEmail("coach99", "new@test.com"))
                .expectNext("Email updated")
                .verifyComplete();
    }

    @Test
    void addSpecialtyToTrainer_postsWithUsernameAndSpecialtyAsPathVariables() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq("coach99"), eq("Yoga"))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Specialty added"));

        StepVerifier.create(repository.addSpecialtyToTrainer("coach99", "Yoga"))
                .expectNext("Specialty added")
                .verifyComplete();
    }

    @Test
    void dessociateSpecialty_deletesWithUsernameAndSpecialtyAsPathVariables() {
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("coach99"), eq("Yoga"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Specialty removed"));

        StepVerifier.create(repository.dessociateSpecialty("coach99", "Yoga"))
                .expectNext("Specialty removed")
                .verifyComplete();
    }

    @Test
    void addClassToTrainer_postsWithUsernameAndClassAsPathVariables() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq("coach99"), eq("Yoga101"))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Class added"));

        StepVerifier.create(repository.addClassToTrainer("coach99", "Yoga101"))
                .expectNext("Class added")
                .verifyComplete();
    }

    @Test
    void dessociateClassToTrainer_deletesWithUsernameAndClassNameAsPathVariables() {
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("coach99"), eq("Yoga101"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Class removed"));

        StepVerifier.create(repository.dessociateClassToTrainer("coach99", "Yoga101"))
                .expectNext("Class removed")
                .verifyComplete();
    }

    @Test
    void deleteClassTrainerByUsernameWithPassword_deletesWithUsernameAndPasswordAsPathVariables() {
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("coach99"), eq("pw"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Deleted"));

        StepVerifier.create(repository.deleteClassTrainerByUsernameWithPassword("coach99", "pw"))
                .expectNext("Deleted")
                .verifyComplete();
    }
}
