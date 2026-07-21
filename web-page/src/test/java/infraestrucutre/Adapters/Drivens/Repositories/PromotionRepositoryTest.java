package infraestrucutre.Adapters.Drivens.Repositories;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.Properties.ServicesUrl;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class PromotionRepositoryTest {

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

    private PromotionRepository repository;

    @BeforeEach
    void setUp() {
        ServicesUrl servicesUrl = new ServicesUrl();
        ServicesUrl.InfoProperties info = new ServicesUrl.InfoProperties();
        info.setUrl(BASE_URL);
        servicesUrl.setInfo(info);

        repository = new PromotionRepository(webClientBuilder, servicesUrl);
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    private Promotion sample() {
        return new Promotion(1, "Summer sale", "1 month", 20, LocalDate.now(), LocalDate.now().plusMonths(1), true);
    }

    @Test
    void getPromotionsForCurrentDate_usesACollectionsSingletonMapUriTemplate() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(org.mockito.ArgumentMatchers.anyString(), any(java.util.Map.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Promotion promotion = sample();
        when(responseSpec.bodyToFlux(Promotion.class)).thenReturn(Flux.just(promotion));

        StepVerifier.create(repository.getPromotionsForCurrentDate("2026-01-01"))
                .expectNext(promotion)
                .verifyComplete();
    }

    @Test
    void getPromotionsForDate_usesACollectionsSingletonMapUriTemplate() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(org.mockito.ArgumentMatchers.anyString(), any(java.util.Map.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Promotion promotion = sample();
        when(responseSpec.bodyToFlux(Promotion.class)).thenReturn(Flux.just(promotion));

        StepVerifier.create(repository.getPromotionsForDate("2026-01-01"))
                .expectNext(promotion)
                .verifyComplete();
    }
}
