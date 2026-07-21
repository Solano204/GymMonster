package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.web.reactive.function.client.WebClient;

import infraestrucutre.Adapters.Drivens.Entities.Equipament;
import infraestrucutre.Adapters.Drivens.Properties.ServicesUrl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class EquipamentInformationClientTest {

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

    private EquipamentInformationClient repository;

    @BeforeEach
    void setUp() {
        ServicesUrl servicesUrl = new ServicesUrl();
        ServicesUrl.InfoProperties info = new ServicesUrl.InfoProperties();
        info.setUrl(BASE_URL);
        servicesUrl.setInfo(info);

        repository = new EquipamentInformationClient(webClientBuilder, servicesUrl);
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    private Equipament sampleEquipament() {
        return new Equipament(1L, "Treadmill", "Cardio machine", LocalDate.now(), LocalDate.now().plusYears(5), "NEW");
    }

    @Test
    void createEquipament_postsToFixedEndpoint_withBody() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        Equipament equipament = sampleEquipament();
        when(requestBodySpec.bodyValue(equipament)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Equipament.class)).thenReturn(Mono.just(equipament));

        StepVerifier.create(repository.createEquipament(equipament))
                .expectNext(equipament)
                .verifyComplete();

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestBodyUriSpec).uri(uriCaptor.capture());
        assertThat(uriCaptor.getValue()).isEqualTo(BASE_URL + "/api/equipaments");
    }

    @Test
    void getAllEquipaments_getsFixedEndpoint_asAFlux() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Equipament equipament = sampleEquipament();
        when(responseSpec.bodyToFlux(Equipament.class)).thenReturn(Flux.just(equipament));

        StepVerifier.create(repository.getAllEquipaments())
                .expectNext(equipament)
                .verifyComplete();
    }

    @Test
    void getEquipamentByName_getsWithNamePathVariable() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("Treadmill"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Equipament equipament = sampleEquipament();
        when(responseSpec.bodyToMono(Equipament.class)).thenReturn(Mono.just(equipament));

        StepVerifier.create(repository.getEquipamentByName("Treadmill"))
                .expectNext(equipament)
                .verifyComplete();
    }

    @Test
    void updateEquipament_putsWithIdPathVariable_andBody() {
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq(1L))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        Equipament equipament = sampleEquipament();
        when(requestBodySpec.bodyValue(equipament)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Equipament.class)).thenReturn(Mono.just(equipament));

        StepVerifier.create(repository.updateEquipament(1L, equipament))
                .expectNext(equipament)
                .verifyComplete();
    }

    @Test
    void deleteEquipamentByName_deletesWithNamePathVariable() {
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("Treadmill"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Deleted"));

        StepVerifier.create(repository.deleteEquipamentByName("Treadmill"))
                .expectNext("Deleted")
                .verifyComplete();
    }
}
