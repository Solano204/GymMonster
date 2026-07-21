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

import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.Properties.ServicesUrl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class PromotionInformationClientImplTest {

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

    private PromotionInformationClientImpl repository;

    @BeforeEach
    void setUp() {
        ServicesUrl servicesUrl = new ServicesUrl();
        ServicesUrl.InfoProperties info = new ServicesUrl.InfoProperties();
        info.setUrl(BASE_URL);
        servicesUrl.setInfo(info);

        repository = new PromotionInformationClientImpl(webClientBuilder, servicesUrl);
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    private Promotion sample() {
        return new Promotion(1, "Summer sale", "1 month", 20, LocalDate.now(), LocalDate.now().plusMonths(1), true);
    }

    @Test
    void getAllPromotions_getsFixedEndpoint_asAFlux() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Promotion promotion = sample();
        when(responseSpec.bodyToFlux(Promotion.class)).thenReturn(Flux.just(promotion));

        StepVerifier.create(repository.getAllPromotions()).expectNext(promotion).verifyComplete();

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestHeadersUriSpec).uri(uriCaptor.capture());
        assertThat(uriCaptor.getValue()).isEqualTo(BASE_URL + "/api/promotions");
    }

    @Test
    void getPromotionsByCurrentDate_getsWithDatePathVariable() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq(date))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Promotion promotion = sample();
        when(responseSpec.bodyToFlux(Promotion.class)).thenReturn(Flux.just(promotion));

        StepVerifier.create(repository.getPromotionsByCurrentDate(date)).expectNext(promotion).verifyComplete();
    }

    @Test
    void getPromotionsByStartDate_getsWithDatePathVariable() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq(date))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Promotion promotion = sample();
        when(responseSpec.bodyToFlux(Promotion.class)).thenReturn(Flux.just(promotion));

        StepVerifier.create(repository.getPromotionsByStartDate(date)).expectNext(promotion).verifyComplete();
    }

    @Test
    void getPromotionsByEndDate_getsWithDatePathVariable() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq(date))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Promotion promotion = sample();
        when(responseSpec.bodyToFlux(Promotion.class)).thenReturn(Flux.just(promotion));

        StepVerifier.create(repository.getPromotionsByEndDate(date)).expectNext(promotion).verifyComplete();
    }

    @Test
    void createPromotion_postsToFixedEndpoint_withBody() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        Promotion promotion = sample();
        when(requestBodySpec.bodyValue(promotion)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Promotion.class)).thenReturn(Mono.just(promotion));

        StepVerifier.create(repository.createPromotion(promotion)).expectNext(promotion).verifyComplete();
    }

    @Test
    void updatePromotion_putsWithIdPathVariable_andBody() {
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq(1))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        Promotion promotion = sample();
        when(requestBodySpec.bodyValue(promotion)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Updated"));

        StepVerifier.create(repository.updatePromotion(1, promotion)).expectNext("Updated").verifyComplete();
    }

    @Test
    void deletePromotionById_deletesWithIdPathVariable() {
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq(1))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Deleted"));

        StepVerifier.create(repository.deletePromotionById(1)).expectNext("Deleted").verifyComplete();
    }
}
