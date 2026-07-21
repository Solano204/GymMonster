package infraestrucutre.Adapters.Drivens.Repositories;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.Properties.ServicesUrl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class PerTrainerRepositoryTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private static final String BASE_URL = "http://server-informations:8082";

    private PerTrainerRepository repository;

    @BeforeEach
    void setUp() {
        ServicesUrl servicesUrl = new ServicesUrl();
        ServicesUrl.InfoProperties info = new ServicesUrl.InfoProperties();
        info.setUrl(BASE_URL);
        servicesUrl.setInfo(info);

        repository = new PerTrainerRepository(webClientBuilder, servicesUrl);
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    void getAllTrainers_buildsUriWithPageAndSizeQueryParams() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        DtoTrainerData trainerData = new DtoTrainerData(1L, "coach99", "coach@test.com", "Ana", "M", "Lopez",
                "Diaz", "35", "170", "65", null, 5);
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
}
