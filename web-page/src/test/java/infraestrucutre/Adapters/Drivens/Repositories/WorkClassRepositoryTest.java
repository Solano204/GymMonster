package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Properties.ServicesUrl;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class WorkClassRepositoryTest {

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

    private WorkClassRepository repository;

    @BeforeEach
    void setUp() {
        ServicesUrl servicesUrl = new ServicesUrl();
        ServicesUrl.InfoProperties info = new ServicesUrl.InfoProperties();
        info.setUrl(BASE_URL);
        servicesUrl.setInfo(info);

        repository = new WorkClassRepository(webClientBuilder, servicesUrl);
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    void getAllWorkClasses_getsFixedEndpoint_asAFlux() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        WorkClass workClass = new WorkClass(1L, "Yoga", "desc", "60min");
        when(responseSpec.bodyToFlux(WorkClass.class)).thenReturn(Flux.just(workClass));

        StepVerifier.create(repository.getAllWorkClasses()).expectNext(workClass).verifyComplete();

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestHeadersUriSpec).uri(uriCaptor.capture());
        assertThat(uriCaptor.getValue()).isEqualTo(BASE_URL + "/api/workclasses");
    }

    @Test
    void getClientsByWorkClassWithPagination_buildsUriWithPageAndSize() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        DtoDetailUserReciving client = new DtoDetailUserReciving("John", "Q", "Doe", "Public", "30");
        when(responseSpec.bodyToFlux(DtoDetailUserReciving.class)).thenReturn(Flux.just(client));

        StepVerifier.create(repository.getClientsByWorkClassWithPagination("Yoga", 0, 10))
                .expectNext(client)
                .verifyComplete();
    }

    @Test
    void getTrainersByWorkClassWithPagination_buildsUriWithPageAndSize() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        DtoDetailUserReciving trainer = new DtoDetailUserReciving("Coach", "Q", "Doe", "Public", "30");
        when(responseSpec.bodyToFlux(DtoDetailUserReciving.class)).thenReturn(Flux.just(trainer));

        StepVerifier.create(repository.getTrainersByWorkClassWithPagination("Yoga", 0, 10))
                .expectNext(trainer)
                .verifyComplete();
    }

    @Test
    void getCSchedulesByWorkClassWithPagination_buildsUri() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Schedule schedule = new Schedule(1, "MONDAY", "08:00", "09:00");
        when(responseSpec.bodyToFlux(Schedule.class)).thenReturn(Flux.just(schedule));

        StepVerifier.create(repository.getCSchedulesByWorkClassWithPagination("Yoga"))
                .expectNext(schedule)
                .verifyComplete();
    }
}
