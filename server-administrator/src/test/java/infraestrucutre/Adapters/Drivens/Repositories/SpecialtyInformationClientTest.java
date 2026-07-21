package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.Properties.ServicesUrl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class SpecialtyInformationClientTest {

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

    private SpecialtyInformationClient repository;

    @BeforeEach
    void setUp() {
        ServicesUrl servicesUrl = new ServicesUrl();
        ServicesUrl.InfoProperties info = new ServicesUrl.InfoProperties();
        info.setUrl(BASE_URL);
        servicesUrl.setInfo(info);

        repository = new SpecialtyInformationClient(webClientBuilder, servicesUrl);
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    private DtoSpecialtyRecived sample() {
        return new DtoSpecialtyRecived("Yoga", "Relaxing class");
    }

    @Test
    void getAllSpecialties_getsFixedEndpoint_asAFlux() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        DtoSpecialtyRecived specialty = sample();
        when(responseSpec.bodyToFlux(DtoSpecialtyRecived.class)).thenReturn(Flux.just(specialty));

        StepVerifier.create(repository.getAllSpecialties()).expectNext(specialty).verifyComplete();

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestHeadersUriSpec).uri(uriCaptor.capture());
        assertThat(uriCaptor.getValue()).isEqualTo(BASE_URL + "/api/specialties");
    }

    @Test
    void getAllTrainers_getsADistinctFixedEndpoint() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        DtoSpecialtyRecived specialty = sample();
        when(responseSpec.bodyToFlux(DtoSpecialtyRecived.class)).thenReturn(Flux.just(specialty));

        StepVerifier.create(repository.getAllTrainers()).expectNext(specialty).verifyComplete();

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestHeadersUriSpec).uri(uriCaptor.capture());
        assertThat(uriCaptor.getValue()).isEqualTo(BASE_URL + "/api/specialties/allTrainers");
    }

    @Test
    void createSpecialty_postsToFixedEndpoint_withBody() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        DtoSpecialtyRecived specialty = sample();
        when(requestBodySpec.bodyValue(specialty)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(DtoSpecialtyRecived.class)).thenReturn(Mono.just(specialty));

        StepVerifier.create(repository.createSpecialty(specialty)).expectNext(specialty).verifyComplete();
    }

    @Test
    void getSpecialtyByName_getsWithNamePathVariable() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("Yoga"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        DtoSpecialtyRecived specialty = sample();
        when(responseSpec.bodyToMono(DtoSpecialtyRecived.class)).thenReturn(Mono.just(specialty));

        StepVerifier.create(repository.getSpecialtyByName("Yoga")).expectNext(specialty).verifyComplete();
    }

    @Test
    void updateSpecialty_putsWithNamePathVariable_andBody() {
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq("Yoga"))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        DtoSpecialtyRecived specialty = sample();
        when(requestBodySpec.bodyValue(specialty)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Updated"));

        StepVerifier.create(repository.updateSpecialty("Yoga", specialty)).expectNext("Updated").verifyComplete();
    }

    @Test
    void deleteSpecialty_deletesWithNamePathVariable() {
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("Yoga"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Deleted"));

        StepVerifier.create(repository.deleteSpecialty("Yoga")).expectNext("Deleted").verifyComplete();
    }
}
